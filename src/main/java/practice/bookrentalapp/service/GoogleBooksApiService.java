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
import practice.bookrentalapp.repositories.BookRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoogleBooksApiService {
    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(GoogleBooksApiService.class);
    private final Integer PAGE_SIZE = 40;
    private Integer copies;

    @Value("${google.books.api.key}")
    private String apiKey;

    @Autowired
    public GoogleBooksApiService(BookRepository bookRepository, RestTemplate restTemplate) {
        this.bookRepository = bookRepository;
        this.restTemplate = restTemplate;
    }

    public List<Long> fetchAndSaveBooks(BookSearchRequestDto requestDto) {
        copies = requestDto.getCopies();
        return requestDto.getSearchGroups().stream()
                .flatMap(group -> processSearchGroup(group).stream())
                .map(Book::getId)
                .collect(Collectors.toList());
    }

    private List<Book> processSearchGroup(SearchGroup group) {
        try {
            List<Book> books = searchGoogleBooks(group);
            List<Book> filteredBooks = filterUniqueBooks(books);
            List<Book> booksToSave = filterExistingBooks(filteredBooks);
            return bookRepository.saveAll(booksToSave);
        } catch (Exception e) {
            logger.error("Error processing search group {}: {}", group, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Book> searchGoogleBooks(SearchGroup searchGroup) {
        List<Book> allBooks = new ArrayList<>();
        int startIndex = 0;
        Integer totalItems = null;

        do {
            GoogleBooksResponseDto response = fetchBooksBatch(getGoogleBooksApiUrl(searchGroup, startIndex));
            if (response == null || response.getItems() == null) {
                break;
            }
            if (totalItems == null) {
                totalItems = response.getTotalItems();
            }
            allBooks.addAll(
                    response.getItems().stream()
                            .map(this::convertToBook)
                            .filter(Objects::nonNull)
                            .toList()
            );
            startIndex += PAGE_SIZE;
        } while (startIndex < totalItems);

        return allBooks;
    }

    private GoogleBooksResponseDto fetchBooksBatch(String url) {
        ResponseEntity<GoogleBooksResponseDto> response = restTemplate.getForEntity(url, GoogleBooksResponseDto.class);
        return response.getBody();
    }

    private String getGoogleBooksApiUrl(SearchGroup searchGroup, int startIndex) {
        StringBuilder url = new StringBuilder("https://www.googleapis.com/books/v1/volumes?q=");
        appendQueryParam(url, "inauthor", searchGroup.getAuthor());
        appendQueryParam(url, "intitle", searchGroup.getTitle());
        appendQueryParam(url, "subject", searchGroup.getCategory());
        appendQueryParam(url, "isbn", searchGroup.getIsbn());
        url.append("&orderBy=newest&startIndex=").append(startIndex)
                .append("&maxResults=").append(PAGE_SIZE)
                .append("&key=").append(apiKey);
        logger.info(url.toString());
        return url.toString();
    }

    private void appendQueryParam(StringBuilder url, String key, String value) {
        if (value != null) {
            url.append("+").append(key).append(":").append(value);
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

    private List<Book> filterExistingBooks(List<Book> books) {
        return books.stream()
                .filter(book -> !bookRepository.existsByTitleOrISBN(book.getTitle(), book.getISBN()))
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

