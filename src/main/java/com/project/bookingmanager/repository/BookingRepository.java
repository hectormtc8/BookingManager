package com.project.bookingmanager.repository;

import com.project.bookingmanager.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.resource.id = :resourceId
        AND b.date = :date
        AND b.status = 'CONFIRMED'
        AND b.startTime < :endTime
        AND b.endTime > :startTime
        """)
    List<Booking> findOverlappingBookings(
            @Param("resourceId") Long resourceId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}