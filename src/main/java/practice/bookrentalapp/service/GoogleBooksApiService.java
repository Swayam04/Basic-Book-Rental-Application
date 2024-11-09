package practice.bookrentalapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import practice.bookrentalapp.dto.BookSearchRequestDto;
import practice.bookrentalapp.dto.BookSearchRequestDto.SearchGroup;
import practice.bookrentalapp.dto.GoogleBooksResponseDto;
import practice.bookrentalapp.entities.Book;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class GoogleBooksApiService {
    private final BookService bookService;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(GoogleBooksApiService.class);
    private final ExecutorService executorService;

    private final Integer PAGE_SIZE = 40;
    private Integer copies;

    @Value("${google.books.api.key}")
    private String apiKey;

    @Autowired
    public GoogleBooksApiService(BookService service, RestTemplate restTemplate, ExecutorService executorService) {
        this.bookService = service;
        this.restTemplate = restTemplate;
        this.executorService = executorService;
    }

    public List<Long> fetchAndSaveBooks(BookSearchRequestDto requestDto) {
        copies = requestDto.getCopies();
        try {
            List<Future<List<Book>>> futures = requestDto.getSearchGroups().stream()
                    .map(group -> executorService.submit(() -> processSearchGroup(group)))
                    .toList();
            List<Book> allBooks = new ArrayList<>();
            for(Future<List<Book>> future : futures) {
                allBooks.addAll(future.get());
            }
            return bookService.saveBooksBatch(allBooks).stream()
                    .map(Book::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error processing request {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Book> processSearchGroup(SearchGroup group) {
        try {
            List<Book> books = searchGoogleBooks(group);
            return filterUniqueBooks(books);
        } catch (Exception e) {
            logger.error("Error processing search group {}: {}", group, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Book> searchGoogleBooks(BookSearchRequestDto.SearchGroup searchGroup) {
        List<Book> allBooks = new ArrayList<>();
        int startIndex = 0;
        Integer totalItems = null;
        do {
            String url = getGoogleBooksApiUrl(searchGroup, startIndex);
            ResponseEntity<GoogleBooksResponseDto> response = restTemplate.getForEntity(url, GoogleBooksResponseDto.class);
            GoogleBooksResponseDto responseBody = response.getBody();

            if (responseBody == null || responseBody.getItems() == null) {
                break;
            }
            if (totalItems == null) {
                totalItems = responseBody.getTotalItems();
            }
            allBooks.addAll(
                    responseBody.getItems().stream()
                            .map(this::convertToBook)
                            .filter(Objects::nonNull)
                            .toList()
            );
            startIndex += PAGE_SIZE;
        } while (startIndex < totalItems);
        return allBooks;
    }

    private String getGoogleBooksApiUrl(BookSearchRequestDto.SearchGroup searchGroup, int startIndex) {
        StringBuilder query = new StringBuilder();
        appendIfPresent(query, "inauthor", searchGroup.getAuthor());
        appendIfPresent(query, "intitle", searchGroup.getTitle());
        appendIfPresent(query, "subject", searchGroup.getCategory());
        appendIfPresent(query, "isbn", searchGroup.getIsbn());

        return String.format("https://www.googleapis.com/books/v1/volumes?q=%s&orderBy=newest&startIndex=%d&maxResults=%d&key=%s",
                query.toString().trim(),
                startIndex,
                PAGE_SIZE,
                apiKey);
    }

    private void appendIfPresent(StringBuilder query, String key, String value) {
        if (value != null && !value.isEmpty()) {
            if (!query.isEmpty()) {
                query.append("+");
            }
            query.append(key).append(":").append(value);
        }
    }

    private List<Book> filterUniqueBooks(List<Book> books) {
        return books.stream()
                .collect(Collectors.groupingBy(
                        Book::getTitle,
                        Collectors.maxBy(Comparator.comparing(
                                book -> book.getPublishedDate() != null ? book.getPublishedDate() : LocalDate.MIN
                        ))
                ))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Book convertToBook(GoogleBooksResponseDto.Item item) {
        GoogleBooksResponseDto.VolumeInfo volumeInfo = item.getVolumeInfo();
        if (volumeInfo == null || !"en".equals(volumeInfo.getLanguage()) || !"BOOK".equals(volumeInfo.getPrintType())) {
            return null;
        }
        Book book = new Book();
        book.setCopies(copies);
        book.setTitle(volumeInfo.getTitle());
        book.setPageCount(volumeInfo.getPageCount());
        book.setLanguage("en");
        book.setAverageRating(volumeInfo.getAverageRating());
        book.setPublisher(volumeInfo.getPublisher());
        book.setAuthors(new HashSet<>(volumeInfo.getAuthors() != null ? volumeInfo.getAuthors() : Collections.emptyList()));
        book.setCategories(new HashSet<>(volumeInfo.getCategories() != null ? volumeInfo.getCategories() : Collections.emptyList()));
        book.setPublishedDate(parsePublishedDate(volumeInfo.getPublishedDate()));
        setISBNIfPresent(volumeInfo, book);
        return book;
    }

    private void setISBNIfPresent(GoogleBooksResponseDto.VolumeInfo volumeInfo, Book book) {
        if(volumeInfo.getIndustryIdentifiers() == null) {
            return;
        }
        for (GoogleBooksResponseDto.VolumeInfo.IndustryIdentifier identifier : volumeInfo.getIndustryIdentifiers()) {
            if ("ISBN_13".equalsIgnoreCase(identifier.getType())) {
                book.setISBN(identifier.getIdentifier());
                break;
            }
        }
    }
    private LocalDate parsePublishedDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return switch (dateStr.length()) {
                case 10 -> LocalDate.parse(dateStr);
                case 7 -> LocalDate.parse(dateStr + "-01");
                case 4 -> LocalDate.parse(dateStr + "-01-01");
                default -> null;
            };
        } catch (DateTimeParseException e) {
            logger.warn("Unable to parse published date: {}", dateStr);
            return null;
        }
    }
}

