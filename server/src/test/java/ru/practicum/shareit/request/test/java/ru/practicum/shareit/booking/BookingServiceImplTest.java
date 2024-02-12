package ru.practicum.shareit.request.test.java.ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private final User user = new User(1L, "username", "email@email.com");
    private final User owner = new User(2L, "username2", "email2@email.com");
    private final UserDto userDto = new UserDto(1L, "username", "email@email.com");
    private final Item item =
            new Item(1L, "item name", "description", true, owner.getId(), null);
    private final Booking booking =
            new Booking(1L,
                    LocalDateTime.now().plusDays(1L),
                    LocalDateTime.now().plusDays(2L),
                    item,
                    user,
                    StatusBooking.APPROVED);
    private final Booking bookingWaiting =
            new Booking(1L,
                    LocalDateTime.now().plusDays(1L),
                    LocalDateTime.now().plusDays(2L),
                    item,
                    user,
                    StatusBooking.WAITING);
    private final BookingDto bookingDto =
            new BookingDto(1L,
                    LocalDateTime.now().plusDays(1L),
                    LocalDateTime.now().plusDays(2L)
            );
    private final BookingDto bookingDtoEndBeforeStart =
            new BookingDto(1L,
                    LocalDateTime.now().plusDays(1L),
                    LocalDateTime.now().minusDays(2L)
            );
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create() {
        BookingDtoResponse expectedBookingDtoOut = BookingMapper.toBookingDtoResponse(BookingMapper.toBooking(bookingDto, user, item));
        when(userService.getUserById(userDto.getId())).thenReturn(user);
        when(itemService.findItemById(anyLong())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.toBooking(bookingDto, user, item));

        BookingDtoResponse actualBookingDtoOut = bookingService.add(userDto.getId(), bookingDto);

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void createWhenEndIsBeforeStartShouldThrowValidationException() {
        when(userService.getUserById(userDto.getId())).thenReturn(user);
        when(itemService.findItemById(anyLong())).thenReturn(item);

        ResponseStatusException bookingValidationException = assertThrows(ResponseStatusException.class,
                () -> bookingService.add(userDto.getId(), bookingDtoEndBeforeStart));

        assertEquals(bookingValidationException.getMessage(), "400 BAD_REQUEST");
    }

    @Test
    void createWhenItemIsNotAvailableShouldThrowValidationException() {
        item.setAvailable(false);
        when(userService.getUserById(userDto.getId())).thenReturn(user);
        when(itemService.findItemById(anyLong())).thenReturn(item);

        ResponseStatusException bookingValidationException = assertThrows(ResponseStatusException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(bookingValidationException.getMessage(), "400 BAD_REQUEST");
    }

    @Test
    void createWhenItemOwnerEqualsBookerShouldThrowValidationException() {
        item.setOwner(user.getId());
        when(userService.getUserById(userDto.getId())).thenReturn(user);
        when(itemService.findItemById(anyLong())).thenReturn(item);

        ResponseStatusException bookingValidationException = assertThrows(ResponseStatusException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(bookingValidationException.getMessage(), "404 NOT_FOUND");
    }

    @Test
    void update() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoResponse actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(StatusBooking.APPROVED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateWhenStatusNotApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDtoResponse actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), false);

        assertEquals(StatusBooking.REJECTED, actualBookingDtoOut.getStatus());
    }

    @Test
    void updateShouldStatusNotWaiting() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ResponseStatusException bookingValidationException = assertThrows(ResponseStatusException.class,
                () -> bookingService.update(owner.getId(), booking.getId(), false));

        assertEquals(bookingValidationException.getMessage(), "400 BAD_REQUEST");
    }

    @Test
    void updateWhenUserIsNotItemOwnerShouldThrowNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ResponseStatusException bookingValidationException = assertThrows(ResponseStatusException.class,
                () -> bookingService.update(userDto.getId(), booking.getId(), true));

        assertEquals(bookingValidationException.getMessage(), "404 NOT_FOUND");
    }

    @Test
    void getById() {
        BookingDtoResponse expectedBookingDtoOut = BookingMapper.toBookingDtoResponse(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoResponse actualBookingDtoOut = bookingService.findBookingByUserId(user.getId(), booking.getId());

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void getByIdWhenBookingIdIsNotValidShouldThrowObjectNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException bookingNotFoundException = assertThrows(ResponseStatusException.class,
                () -> bookingService.findBookingByUserId(1L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "404 NOT_FOUND");
    }

    @Test
    void getByIdWhenUserIsNotItemOwnerShouldThrowObjectNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ResponseStatusException bookingNotFoundException = assertThrows(ResponseStatusException.class,
                () -> bookingService.findBookingByUserId(3L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "404 NOT_FOUND");
    }

    @Test
    void getAllByBookerWhenBookingStateAll() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllBookingsByBookerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAll(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateCURRENT() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllCurrentBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAll(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStatePAST() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllPastBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAll(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateFUTURE() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllFutureBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAll(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateWAITING() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllWaitingBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAll(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateIsNotValidShouldThrowIllegalArgumentException() {
        assertThrows(ValidationException.class,
                () -> bookingService.findAll(user.getId(), "404 NOT_FOUND", 0, 10));
    }

    @Test
    void getAllByOwnerWhenBookingStateAll() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllBookingsByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateCURRENT() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllCurrentBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStatePAST() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllPastBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateFUTURE() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllFutureBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateWAITING() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllWaitingBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateREJECTED() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllRejectedBookingsByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBookerWhenBookingStateREJECTED() {
        List<BookingDtoResponse> expectedBookingsDtoOut = List.of(BookingMapper.toBookingDtoResponse(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.findAllRejectedBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> actualBookingsDtoOut = bookingService.findAll(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateIsNotValidThenThrowIllegalArgumentException() {
        when(userService.getUserById(user.getId())).thenReturn(user);

        assertThrows(ValidationException.class,
                () -> bookingService.findAllOwner(user.getId(), "Error", 0, 10));
    }
}
