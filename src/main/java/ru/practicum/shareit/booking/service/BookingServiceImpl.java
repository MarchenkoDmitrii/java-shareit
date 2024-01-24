package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.ValidationException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDtoOut add(Long userId, BookingDto bookingDto) {
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        validate(bookingDto, user, item);
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long userId, Long bookingId, Boolean approved) {
        validateBookingDetails(userId, bookingId, 1);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        StatusBooking newStatus = approved ? StatusBooking.APPROVED : StatusBooking.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut findBookingByUserId(Long userId, Long bookingId) {
        validateBookingDetails(userId, bookingId, 2);
        return BookingMapper.toBookingDtoOut(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAll(Long userId, String state) {
        userService.getUserById(userId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .sorted((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()))
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .sorted((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()))
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .sorted((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException();
        }
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAllOwner(Long userId, String state) {
        userService.getUserById(userId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException();
        }
    }

    private void validate(BookingDto bookingDto, User user, Item item) {
        if (Optional.ofNullable(bookingDto.getEnd()).isEmpty()
                || Optional.ofNullable(bookingDto.getStart()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!item.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (user.getId().equals(item.getOwner())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
                || (bookingDto.getStart().isBefore(LocalDateTime.now()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void validateBookingDetails(Long userId, Long bookingId, Integer number) {
        Booking bookingById = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Item item = bookingById.getItem();
        switch (number) {
            case 1:
                if (!item.getOwner().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
                if (!bookingById.getStatus().equals(StatusBooking.WAITING)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            case 2:
                if (!bookingById.getBooker().getId().equals(userId)
                        && !item.getOwner().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
        }
    }

    private BookingState validState(String bookingState) {
        BookingState state = BookingState.from(bookingState);
        if (state == null) {
            throw new ValidationException();
        }
        return state;
    }


}
