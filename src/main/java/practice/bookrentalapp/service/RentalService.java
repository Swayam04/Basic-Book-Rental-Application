package practice.bookrentalapp.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import practice.bookrentalapp.model.dto.entityDtos.RentalDto;
import practice.bookrentalapp.model.dto.request.CreateRentalRequest;
import practice.bookrentalapp.model.dto.request.RentalFilter;
import practice.bookrentalapp.model.entities.Book;
import practice.bookrentalapp.model.entities.Rental;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.model.enums.RentalStatus;
import practice.bookrentalapp.repositories.BookRepository;
import practice.bookrentalapp.repositories.RentalRepository;
import practice.bookrentalapp.utils.EntityDtoMapper;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RentalService {
    @PersistenceContext
    private EntityManager entityManager;
    private final RentalRepository rentalRepository;
    private final EntityDtoMapper entityDtoMapper;
    private final BookRepository bookRepository;

    @Autowired
    public RentalService(RentalRepository rentalRepository, EntityDtoMapper entityDtoMapper, BookRepository bookRepository) {
        this.rentalRepository = rentalRepository;
        this.entityDtoMapper = entityDtoMapper;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public RentalDto getRentalById(Long id, User user) {
        Rental rental = rentalRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        if(!Objects.equals(rental.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("User is not the owner of rental");
        }
        updateOverdueStatus(rental);
        return entityDtoMapper.mapToRentalDto(rentalRepository.save(rental));
    }

    @Transactional
    public Page<RentalDto> getRentalsForUser(User user, RentalFilter rentalFilter) {
        rentalFilter.setUserId(user.getId());
        return getRentals(rentalFilter);
    }

    @Transactional
    public Page<RentalDto> getRentals(RentalFilter rentalFilter) {
        Pageable pageable = PageRequest.of(
                rentalFilter.getPage(),
                rentalFilter.getSize(),
                Sort.by(Sort.Direction.fromString(rentalFilter.getDir()), rentalFilter.getOrderBy())
        );
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Rental> cq = cb.createQuery(Rental.class);
        Root<Rental> from = cq.from(Rental.class);
        List<Predicate> predicates = buildPredicates(from, rentalFilter, cb);

        cq.select(from).where(predicates.toArray(new Predicate[0])).distinct(true);
        cq.orderBy(QueryUtils.toOrders(pageable.getSort(), from, cb));

        TypedQuery<Rental> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Rental> rentals = query.getResultList();

        rentals.forEach(this::updateOverdueStatus);
        rentalRepository.saveAll(rentals);

        List<RentalDto> rentalDtos = rentals.stream()
                .map(entityDtoMapper::mapToRentalDto)
                .collect(Collectors.toList());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Rental> countRoot = countQuery.from(Rental.class);
        List<Predicate> countPredicates = buildPredicates(countRoot, rentalFilter, cb);
        countQuery.select(cb.countDistinct(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(rentalDtos, pageable, total);
    }

    private List<Predicate> buildPredicates(Root<Rental> from, RentalFilter rentalFilter, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (rentalFilter.getUserId() != null) {
            Join<Rental, User> userJoin = from.join("user", JoinType.INNER);
            predicates.add(cb.equal(userJoin.get("id"), rentalFilter.getUserId()));
        }
        if (StringUtils.hasText(rentalFilter.getBookTitle())) {
            Join<Rental, Book> bookJoin = from.join("books", JoinType.INNER);
            predicates.add(cb.like(bookJoin.get("title"), "%" + rentalFilter.getBookTitle() + "%"));
        }
        if(rentalFilter.getStatus() != null) {
            predicates.add(cb.equal(from.get("status").as(RentalStatus.class), rentalFilter.getStatus()));
        }
        if(rentalFilter.getIssueDateBefore() != null) {
            predicates.add(cb.lessThan(from.get("issueDate").as(LocalDate.class), rentalFilter.getIssueDateBefore()));
        }
        if(rentalFilter.getIssueDateAfter() != null) {
            predicates.add(cb.greaterThan(from.get("issueDate").as(LocalDate.class), rentalFilter.getIssueDateAfter()));
        }
        if(rentalFilter.getDueDateBefore() != null) {
            predicates.add(cb.lessThan(from.get("dueDate").as(LocalDate.class), rentalFilter.getDueDateBefore()));
        }
        if(rentalFilter.getDueDateAfter() != null) {
            predicates.add(cb.greaterThan(from.get("dueDate").as(LocalDate.class), rentalFilter.getDueDateAfter()));
        }
        return predicates;
    }

    private void updateOverdueStatus(Rental rental) {
        if(rental.getReturnDate() == null && rental.getDueDate().isBefore(LocalDate.now())) {
            rental.setStatus(RentalStatus.OVERDUE);
        }

    }

    @Transactional
    public RentalDto createRental(CreateRentalRequest request, User user) {
        Set<Book> books = new HashSet<>();
        for(Long id : request.getBookIds()) {
            Book book = bookRepository.findById(id).orElseThrow(IllegalArgumentException::new);
            if(Objects.equals(book.getTotalCopies(), book.getCopiesLent())) {
                throw new IllegalArgumentException("No available copies for book " + book.getTitle());
            }
            book.setCopiesLent(book.getCopiesLent() + 1);
            books.add(book);
            bookRepository.saveAndFlush(book);
        }
        Rental rental = new Rental();
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setBooks(books);
        rental.setUser(user);
        rental.setIssueDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays((long) request.getRentalDuration()));
        return entityDtoMapper.mapToRentalDto(rentalRepository.save(rental));
    }

    @Transactional
    public void closeRental(Long rentalId, User user) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(IllegalArgumentException::new);
        if(!Objects.equals(rental.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("User is not the owner of rental");
        }
        for(Book book : rental.getBooks()) {
            book.setCopiesLent(book.getCopiesLent() - 1);
            bookRepository.saveAndFlush(book);
        }
        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDate.now());
        rentalRepository.save(rental);
    }

}
