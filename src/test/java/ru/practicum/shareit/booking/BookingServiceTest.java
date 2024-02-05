package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {
    private final UserDto userDto1 = new UserDto(null, "username1", "email1@email.com");
    private final UserDto userDto2 = new UserDto(null, "username2", "email2@email.com");
    private final ItemDto itemDto1 =
            new ItemDto(null, "item1 name", "item1 description", true, null);
    private final ItemDto itemDto2 =
            new ItemDto(null, "item2 name", "item2 description", true, null);
    private final BookingDto bookingDto1 =
            new BookingDto(2L, LocalDateTime.now().plusDays(1L), LocalDateTime.now().plusDays(2L));
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    void addBooking() {
        User addedUser1 = userService.saveUser(userDto1);
        User addedUser2 = userService.saveUser(userDto2);
        itemService.createItem(addedUser1.getId(), itemDto1);
        itemService.createItem(addedUser2.getId(), itemDto2);

        BookingDtoResponse bookingDtoOut1 = bookingService.add(addedUser1.getId(), bookingDto1);
        BookingDtoResponse bookingDtoOut2 = bookingService.add(addedUser1.getId(), bookingDto1);

        assertEquals(1L, bookingDtoOut1.getId());
        assertEquals(2L, bookingDtoOut2.getId());
        assertEquals(StatusBooking.WAITING, bookingDtoOut1.getStatus());
        assertEquals(StatusBooking.WAITING, bookingDtoOut2.getStatus());

        BookingDtoResponse updatedBookingDto1 = bookingService.update(addedUser2.getId(),
                bookingDtoOut1.getId(), true);
        BookingDtoResponse updatedBookingDto2 = bookingService.update(addedUser2.getId(),
                bookingDtoOut2.getId(), true);

        assertEquals(StatusBooking.APPROVED, updatedBookingDto1.getStatus());
        assertEquals(StatusBooking.APPROVED, updatedBookingDto2.getStatus());

        List<BookingDtoResponse> bookingsDtoOut = bookingService.findAllOwner(addedUser2.getId(),
                BookingState.ALL.toString(), 0, 10);

        assertEquals(2, bookingsDtoOut.size());
    }

    @Test
    void update_whenBookingIdAndUserIdIsNotValid_thenThrowObjectNotFoundException() {
        Long userId = 3L;
        Long bookingId = 3L;

        Assertions
                .assertThrows(ResponseStatusException.class,
                        () -> bookingService.update(userId, bookingId, true));
    }

}
