package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoOutItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {

        return new BookingDto(
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        return new BookingDtoOut(
                booking.getId(),
                ItemMapper.toItemDtoOut(booking.getItem()),
                convertLocalDateTimeToString(booking.getStart()),
                convertLocalDateTimeToString(booking.getEnd()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setId(booking.getId());
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(StatusBooking.WAITING);
        return booking;
    }

    public static BookingDtoOutItem toBookingDtoOutItem(BookingDtoOut booking) {
        return new BookingDtoOutItem(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    private static String convertLocalDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : "undefined";
    }

}
