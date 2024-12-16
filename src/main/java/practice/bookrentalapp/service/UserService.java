package practice.bookrentalapp.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.dto.request.UpdateUserProfileRequest;
import practice.bookrentalapp.model.dto.request.UserFilter;
import practice.bookrentalapp.model.dto.response.UpdateUserProfileResponse;
import practice.bookrentalapp.model.entities.Book;
import practice.bookrentalapp.model.entities.Rental;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.model.enums.RentalStatus;
import practice.bookrentalapp.model.enums.Role;
import practice.bookrentalapp.repositories.UserRepository;
import practice.bookrentalapp.utils.EntityDtoMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final EntityDtoMapper entityDtoMapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository, EntityDtoMapper entityDtoMapper) {
        this.userRepository = userRepository;
        this.entityDtoMapper = entityDtoMapper;
    }

    public void promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        user.setRole(Role.ROLE_ADMIN);
        log.info("User {} promoted to admin.", user.getUsername());
        userRepository.save(user);
    }

    public UserDto getCurrentUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(entityDtoMapper::mapToUserDto).orElse(null);
    }

    @Transactional
    public UpdateUserProfileResponse updateUser(long userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UpdateUserProfileResponse response = new UpdateUserProfileResponse();

        updateField(request.getNewName(), user::setName, response::setUpdatedName, user::getName);
        updateField(request.getNewEmail(), user::setEmail, response::setUpdatedEmail, user::getEmail);
        updateField(request.getNewUsername(), user::setUsername, response::setUpdatedUsername, user::getUsername);

        userRepository.save(user);
        return response;
    }

    private void updateField(String newValue, Consumer<String> setter, Consumer<String> responseSetter, Supplier<String> getter) {
        if (newValue != null) {
            setter.accept(newValue);
            responseSetter.accept(getter.get());
        } else {
            responseSetter.accept("No Change");
        }

    }

    public UserDto getUser(long userId) {
        return userRepository.findById(userId).map(entityDtoMapper::mapToUserDto).orElseThrow(IllegalArgumentException::new);
    }

    public Page<UserDto> findUsersWithFilter(UserFilter userFilter) {
        if (userFilter == null) {
            userFilter = new UserFilter();
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> from = cq.from(User.class);
        Join<User, Rental> rentalJoin = from.join("rentals", JoinType.INNER);
        Join<Rental, Book> bookJoin = rentalJoin.join("books", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(userFilter.getUsername())) {
            predicates.add(cb.like(from.get("name"), "%" + userFilter.getName() + "%"));
        }
        if (StringUtils.hasText(userFilter.getEmail())) {
            predicates.add(cb.equal(from.get("email"), userFilter.getEmail()));
        }
        if (StringUtils.hasText(userFilter.getUsername())) {
            predicates.add(cb.like(from.get("username"), "%" + userFilter.getUsername() + "%"));
        }
        if (userFilter.getRole() != null) {
            predicates.add(cb.equal(from.get("role").as(Role.class), userFilter.getRole())); //Enum
        }
        if (StringUtils.hasText(userFilter.getRentedBookTitle())) {
            predicates.add(cb.like(bookJoin.get("title"), userFilter.getRentedBookTitle() + "%"));
        }
        if (userFilter.getRentedBookId() != null) {
            predicates.add(cb.equal(bookJoin.get("id"), userFilter.getRentedBookId()));
        }
        if (userFilter.getRentalId() != null) {
            predicates.add(cb.equal(rentalJoin.get("id"), userFilter.getRentalId()));
        }
        if (userFilter.getRentalStatus() != null) {
            predicates.add(cb.equal(rentalJoin.get("status").as(RentalStatus.class), userFilter.getRentalStatus()));
        }
        if (userFilter.getRentedAfter() != null) {
            predicates.add(cb.greaterThan(rentalJoin.get("issueDate").as(LocalDate.class), userFilter.getRentedAfter()));
        }
        if (userFilter.getRentedBefore() != null) {
            predicates.add(cb.lessThan(rentalJoin.get("issueDate").as(LocalDate.class), userFilter.getRentedBefore()));
        }
        if (userFilter.getRentalsGreaterThan() != null) {
            cq.groupBy(from.get("id"));
            cq.having(cb.greaterThan(cb.count(rentalJoin), Long.valueOf(userFilter.getRentalsGreaterThan())));
        }

        if (predicates.isEmpty()) {
            cq.select(from);
        } else {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Pageable pageable = PageRequest.of(userFilter.getPage(),
                userFilter.getSize(),
                userFilter.getDir().equalsIgnoreCase("asc")
                        ? Sort.by(userFilter.getOrderBy()).ascending()
                        : Sort.by(userFilter.getOrderBy()).descending()
        );

        cq.orderBy(pageable.getSort().stream()
                .map(order -> order.isAscending()
                        ? cb.asc(from.get(order.getProperty()))
                        : cb.desc(from.get(order.getProperty())))
                .collect(Collectors.toList()));

        TypedQuery<User> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<UserDto> users = query.getResultList().stream().map(entityDtoMapper::mapToUserDto).toList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);
        countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(users, pageable, total);
    }

}
