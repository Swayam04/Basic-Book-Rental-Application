package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.bookrentalapp.exceptions.UserNotAuthenticatedException;
import practice.bookrentalapp.model.dto.entityDtos.RentalDto;
import practice.bookrentalapp.model.dto.request.CreateRentalRequest;
import practice.bookrentalapp.model.dto.request.RentalFilter;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.service.RentalService;

import static practice.bookrentalapp.utils.LoginChecker.isAuthenticated;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<Page<RentalDto>> getRentals(@Valid RentalFilter rentalFilter) {
        User loggedInUser = isAuthenticated();
        if (loggedInUser == null) {
            throw new UserNotAuthenticatedException("User is not logged in");
        }

        Page<RentalDto> rentals = rentalService.getRentalsForUser(loggedInUser, rentalFilter);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("{id}")
    public ResponseEntity<RentalDto> getRental(@PathVariable Long id) {
        User loggedInUser = isAuthenticated();
        if (loggedInUser == null) {
            throw new UserNotAuthenticatedException("User is not logged in");
        }

        RentalDto rental = rentalService.getRentalById(id, loggedInUser);
        return ResponseEntity.ok(rental);
    }

    @PostMapping
    public ResponseEntity<RentalDto> createRental(@Valid @RequestBody CreateRentalRequest createRentalRequest) {
        User loggedInUser = isAuthenticated();
        if (loggedInUser == null) {
            throw new UserNotAuthenticatedException("User is not logged in");
        }
        RentalDto rental = rentalService.createRental(createRentalRequest, loggedInUser);
        return ResponseEntity.ok(rental);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<String> closeRental(@PathVariable Long id) {
        User loggedInUser = isAuthenticated();
        if (loggedInUser == null) {
            throw new UserNotAuthenticatedException("User is not logged in");
        }
        rentalService.closeRental(id, loggedInUser);
        return ResponseEntity.ok("Rental has been closed");
    }
}
