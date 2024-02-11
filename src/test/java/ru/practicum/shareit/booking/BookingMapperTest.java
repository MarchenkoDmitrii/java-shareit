package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    User user = new User(1L, "username", "email@email.com");
    Item item = new Item(1L, "itemName", "decs", true, user.getId(), null);

    Booking booking =
            new Booking(1L,
                    LocalDateTime.now().plusDays(1L),
                    LocalDateTime.now().plusDays(2L),
                    item,
                    user,
                    StatusBooking.WAITING);

    @Test
    void toBookingItemDto() {
        BookingDtoResponse actualBookingItemDto = BookingMapper.toBookingDtoResponse(booking);

        assertEquals(1L, actualBookingItemDto.getId());
        assertEquals(1L, actualBookingItemDto.getBooker().getId());
    }
}
