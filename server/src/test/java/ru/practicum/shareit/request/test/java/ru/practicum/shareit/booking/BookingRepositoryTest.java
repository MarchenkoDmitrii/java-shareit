package ru.practicum.shareit.request.test.java.ru.practicum.shareit.booking;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ItemRepository itemRepo;

    private Item item;
    private User booker;
    private User user;
    private Booking booking;

    @BeforeEach
    public void init() {
        user = userRepo.save(new User(1L, "username", "email@email.com"));
        booker = userRepo.save(new User(2L, "username1", "email1@email.com"));
        item = itemRepo.save(new Item(1L, "item name", "description", true, user.getId(), null));
    }

    @AfterEach
    @Transactional
    public void tearDown() {
        bookingRepository.deleteAll();
        itemRepo.deleteAll();
        userRepo.deleteAll();

    }

    @Test
    void findAllBookingsByBookerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.APPROVED));
        List<Booking> bookings = bookingRepository.findAllBookingsByBookerId(booker.getId(), PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void findAllCurrentBookingsByBookerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.APPROVED));
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByBookerId(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
    }

    @Test
    void findAllPastBookingsByBookerId() {

        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(3L),
                LocalDateTime.now().minusDays(2L),
                item,
                booker,
                StatusBooking.APPROVED));
        List<Booking> bookings = bookingRepository.findAllPastBookingsByBookerId(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllFutureBookingsByBookerId() {

        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.APPROVED));
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByBookerId(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
    }

    @Test
    void findAllWaitingBookingsByBookerId() {
        bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.WAITING));
        List<Booking> bookings = bookingRepository.findAllWaitingBookingsByBookerId(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), StatusBooking.WAITING);
    }

    @Test
    void findAllRejectedBookingsByBookerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.REJECTED));

        List<Booking> bookings = bookingRepository.findAllRejectedBookingsByBookerId(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), StatusBooking.REJECTED);
    }

    @Test
    void findAllByOwnerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.WAITING));

        bookingRepository.save(new Booking(2L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.WAITING));
        List<Booking> bookings = bookingRepository.findAllBookingsByOwnerId(user.getId(), PageRequest.of(0, 10));

        assertEquals(bookings.size(), 2);
    }

    @Test
    void findAllCurrentBookingsByOwnerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.WAITING));
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByOwnerId(user.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner(), user.getId());
    }

    @Test
    void findAllPastBookingsByOwnerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().minusDays(1L),
                item,
                booker,
                StatusBooking.WAITING));
        List<Booking> bookings = bookingRepository.findAllPastBookingsByOwnerId(user.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner(), user.getId());
    }

    @Test
    void findAllFutureBookingsByOwnerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.WAITING));
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByOwnerId(user.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner(), user.getId());
    }

    @Test
    void findAllWaitingBookingsByOwnerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.WAITING));

        List<Booking> bookings = bookingRepository.findAllWaitingBookingsByOwnerId(user.getId(), LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), StatusBooking.WAITING);
    }

    @Test
    void findAllRejectedBookingsByOwnerId() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.REJECTED));
        List<Booking> bookings = bookingRepository.findAllRejectedBookingsByOwnerId(user.getId(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), StatusBooking.REJECTED);
    }

    @Test
    void findAllByUserBookings() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().minusDays(1L),
                item,
                user,
                StatusBooking.APPROVED));

        List<Booking> bookings = bookingRepository.findAllByUserBookings(user.getId(), item.getId(), LocalDateTime.now());

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), StatusBooking.APPROVED);
    }

    @Test
    void getLastBooking() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now().minusDays(1L),
                item,
                booker,
                StatusBooking.APPROVED));

        Optional<Booking> bookingOptional = bookingRepository.getLastBooking(item.getId(), LocalDateTime.now());
        Booking actualBooking;

        if (bookingOptional.isPresent()) {
            actualBooking = bookingOptional.get();

            assertEquals(actualBooking.getId(), bookingOptional.get().getId());
        } else {
            fail();
        }
    }

    @Test
    void getNextBooking() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.now().plusDays(1L),
                LocalDateTime.now().plusDays(2L),
                item,
                booker,
                StatusBooking.APPROVED));
        Optional<Booking> bookingOptional = bookingRepository.getNextBooking(item.getId(), LocalDateTime.now());
        Booking actualBooking;

        if (bookingOptional.isPresent()) {
            actualBooking = bookingOptional.get();

            assertEquals(actualBooking.getId(), bookingOptional.get().getId());
        } else {
            fail();
        }
    }

}
