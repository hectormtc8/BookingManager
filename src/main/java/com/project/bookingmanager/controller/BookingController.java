package com.project.bookingmanager.controller;

import com.project.bookingmanager.dto.BookingRequest;
import com.project.bookingmanager.model.Booking;
import com.project.bookingmanager.security.UserDetailsImpl;
import com.project.bookingmanager.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Crear, consultar y cancelar reservas de salas")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> create(@Valid @AuthenticationPrincipal UserDetailsImpl principal,
                                          @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(
                principal.getId(), request.resourceId(), request.date(),
                request.startTime(), request.endTime());
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Booking>> getMyBookings(@Valid @AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(principal.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAll() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@Valid @PathVariable Long id,
                                       @AuthenticationPrincipal UserDetailsImpl principal) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        bookingService.cancelBooking(id, principal.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}