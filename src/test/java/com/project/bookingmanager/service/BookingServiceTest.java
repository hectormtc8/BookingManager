package com.project.bookingmanager.service;

import com.project.bookingmanager.exception.BookingConflictException;
import com.project.bookingmanager.exception.InvalidBookingException;
import com.project.bookingmanager.exception.ResourceNotFoundException;
import com.project.bookingmanager.model.Booking;
import com.project.bookingmanager.model.BookingStatus;
import com.project.bookingmanager.model.Resource;
import com.project.bookingmanager.model.Role;
import com.project.bookingmanager.model.User;
import com.project.bookingmanager.repository.BookingRepository;
import com.project.bookingmanager.repository.ResourceRepository;
import com.project.bookingmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private ResourceRepository resourceRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private BookingService bookingService;

    private final LocalDate date = LocalDate.of(2026, 10, 5);
    private final LocalTime start = LocalTime.of(10, 0);
    private final LocalTime end = LocalTime.of(11, 0);

    private User user;
    private Resource room;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setRole(Role.USER);

        room = new Resource();
        room.setId(10L);
        room.setName("Sala A");
        room.setActive(true);
    }

    // ---------- createBooking ----------

    @Test
    void createBooking_withFreeSlot_savesConfirmedBooking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(room));
        when(bookingRepository.findOverlappingBookings(10L, date, start, end)).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(1L, 10L, date, start, end);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getResource()).isEqualTo(room);
        assertThat(result.getStartTime()).isEqualTo(start);
        assertThat(result.getEndTime()).isEqualTo(end);
    }

    @Test
    void createBooking_withOverlappingBooking_throwsConflictAndDoesNotSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(room));
        when(bookingRepository.findOverlappingBookings(10L, date, start, end))
                .thenReturn(List.of(new Booking()));

        assertThatThrownBy(() -> bookingService.createBooking(1L, 10L, date, start, end))
                .isInstanceOf(BookingConflictException.class);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @ParameterizedTest
    @CsvSource({"11:00,10:00", "10:00,10:00"})
    void createBooking_withInvalidTimeRange_throwsInvalidBooking(LocalTime from, LocalTime to) {
        assertThatThrownBy(() -> bookingService.createBooking(1L, 10L, date, from, to))
                .isInstanceOf(InvalidBookingException.class);

        // La validación del rango ocurre antes de tocar la base de datos
        verifyNoInteractions(userRepository, resourceRepository, bookingRepository);
    }

    @Test
    void createBooking_withInactiveRoom_throwsInvalidBooking() {
        room.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> bookingService.createBooking(1L, 10L, date, start, end))
                .isInstanceOf(InvalidBookingException.class);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_withUnknownUser_throwsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(1L, 10L, date, start, end))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createBooking_withUnknownRoom_throwsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(resourceRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(1L, 10L, date, start, end))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- cancelBooking ----------

    private Booking confirmedBookingOf(User owner) {
        Booking booking = new Booking();
        booking.setId(5L);
        booking.setUser(owner);
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    @Test
    void cancelBooking_byOwner_marksAsCancelled() {
        Booking booking = confirmedBookingOf(user);
        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(5L, 1L, false);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_byAdmin_canCancelSomeoneElsesBooking() {
        Booking booking = confirmedBookingOf(user);
        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(5L, 99L, true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_byAnotherUser_throwsAndKeepsBookingConfirmed() {
        Booking booking = confirmedBookingOf(user);
        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(5L, 2L, false))
                .isInstanceOf(InvalidBookingException.class);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_withUnknownId_throwsNotFound() {
        when(bookingRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelBooking(5L, 1L, false))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}