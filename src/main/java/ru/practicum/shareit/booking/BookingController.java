package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoResponse> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestBody BookingDto bookingDto) {
        BookingDtoResponse booking = bookingService.add(userId, bookingDto);
        return ResponseEntity.status(200).body(booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> updateBooking(@PathVariable Long bookingId,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam("approved") Boolean aprove) {
        if (userId == null) {
            return ResponseEntity.status(500).build();
        }
        BookingDtoResponse updateBooking = bookingService.update(userId, bookingId, aprove);
        return ResponseEntity.status(200).body(updateBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoResponse> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @PathVariable("bookingId") Long bookingId) {
        return ResponseEntity.status(200)
                .body(bookingService.findBookingByUserId(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoResponse>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        return ResponseEntity.status(200).body(new ArrayList<>(bookingService.findAll(userId, bookingState)));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoResponse>> getAllOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                                @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        return ResponseEntity.status(200).body(new ArrayList<>(bookingService.findAllOwner(ownerId, bookingState)));
    }

}