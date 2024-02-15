package ru.practicum.shareit.request.test.java.ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private final User user = new User(1L, "username", "email@email.com");
    private final Item item =
            new Item(1L, "item name", "description", false, user.getId(), null);
    private final BookingDtoResponse bookingDtoResponse =
            new BookingDtoResponse(1L, ItemMapper.toItemDtoResponse(item),
                    LocalDateTime.now().plusDays(1L).toString(),
                    LocalDateTime.now().plusDays(2L).toString(),
                    UserMapper.toUserDto(user),
                    StatusBooking.WAITING);
    private final BookingDto bookingDto =
            new BookingDto(1L, LocalDateTime.now().plusDays(1L), LocalDateTime.now().plusDays(2L));
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @Test
    @SneakyThrows
    void createBookingWhenBookingIsValid() {
        when(bookingService.add(user.getId(), bookingDto)).thenReturn(bookingDtoResponse);

        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @Test
    @SneakyThrows
    void updateWhenBookingIsValid() {
        Boolean approved = true;
        Long bookingId = 1L;

        when(bookingService.update(user.getId(), bookingId, approved)).thenReturn(bookingDtoResponse);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @Test
    @SneakyThrows
    void getByIdWhenBookingIsValid() {
        Long bookingId = 1L;

        when(bookingService.findBookingByUserId(user.getId(), bookingId)).thenReturn(bookingDtoResponse);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", user.getId())).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @Test
    @SneakyThrows
    void getAllShouldReturnStatusIsOk() {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";

        when(bookingService.findAll(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingDtoResponse));

        String result = mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDtoResponse)), result);
    }


    @Test
    @SneakyThrows
    void getAllByOwner() {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";

        when(bookingService.findAllOwner(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingDtoResponse));

        String result = mockMvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDtoResponse)), result);
    }
}
