package ru.practicum.shareit.booking.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(@Lazy ItemService itemService, UserService userService, BookingRepository bookingRepository) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public BookingDtoResponse add(Long userId, BookingDto bookingDto) {
        User user = userService.getUserById(userId);
        Item item = itemService.findItemById(bookingDto.getItemId());
        validate(bookingDto, user, item);
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse update(Long userId, Long bookingId, Boolean approved) {
        validateBookingDetails(userId, bookingId, 1);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        StatusBooking newStatus = approved ? StatusBooking.APPROVED : StatusBooking.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse findBookingByUserId(Long userId, Long bookingId) {
        validateBookingDetails(userId, bookingId, 2);
        return BookingMapper.toBookingDtoResponse(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> findAll(Long userId, String state, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        userService.getUserById(userId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(userId, pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .sorted((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()))
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .sorted((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()))
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .sorted((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException();
        }
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> findAllOwner(Long userId, String state, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        userService.getUserById(userId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(userId, pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .sorted((o1, o2) -> Math.toIntExact(o2.getId() - o1.getId()))
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(userId, pageable).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException();
        }
    }

    @Override
    public List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, StatusBooking status) {
        return bookingRepository.findAllByItemInAndStatusOrderByStartAsc(items, status);
    }

    @Override
    public List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, StatusBooking bookingStatus) {
        return bookingRepository.findAllByItemAndStatusOrderByStartAsc(item, bookingStatus);
    }

    @Override
    public List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now) {
        return bookingRepository.findAllByUserBookings(userId, itemId, now);
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
