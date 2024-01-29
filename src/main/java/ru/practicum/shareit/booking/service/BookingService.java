package ru.practicum.shareit.booking.service;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDtoResponse add(Long userId, BookingDto bookingDto);

    BookingDtoResponse update(Long userId, Long bookingId, Boolean approved);

    BookingDtoResponse findBookingByUserId(Long userId, Long bookingId);

    List<BookingDtoResponse> findAll(Long userId, String state);

    List<BookingDtoResponse> findAllOwner(Long userId, String state);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, StatusBooking status);

    List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, StatusBooking bookingStatus);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND i.id = ?2 " +
            "AND b.status = 'APPROVED' " +
            "AND b.end_date < ?3 ", nativeQuery = true)
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);

}
