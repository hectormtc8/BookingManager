package com.project.bookingmanager.service;

import com.project.bookingmanager.exception.BookingConflictException;
import com.project.bookingmanager.exception.InvalidBookingException;
import com.project.bookingmanager.exception.ResourceNotFoundException;
import com.project.bookingmanager.model.Booking;
import com.project.bookingmanager.model.BookingStatus;
import com.project.bookingmanager.model.Resource;
import com.project.bookingmanager.model.User;
import com.project.bookingmanager.repository.BookingRepository;
import com.project.bookingmanager.repository.ResourceRepository;
import com.project.bookingmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public Booking createBooking(Long userId, Long resourceId, LocalDate date,
                                 LocalTime startTime, LocalTime endTime) {

        if (!startTime.isBefore(endTime)) {
            throw new InvalidBookingException("La hora de inicio debe ser anterior a la hora de fin");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada"));

        if (!resource.getActive()) {
            throw new InvalidBookingException("La sala no está disponible");
        }

        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                resourceId, date, startTime, endTime);

        if (!overlapping.isEmpty()) {
            throw new BookingConflictException("La sala ya está reservada en ese horario");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setDate(date);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Long bookingId, Long requesterId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!isAdmin && !booking.getUser().getId().equals(requesterId)) {
            throw new InvalidBookingException("No tienes permiso para cancelar esta reserva");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}