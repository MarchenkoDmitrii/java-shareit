package ru.practicum.shareit.request.test.java.ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private final User user = new User(1L, "username1", "email1@email.com");
    private final ItemDtoResponse item =
            new ItemDtoResponse(1L, "item1 name", "item1 description", true);
    private final ItemRequestDtoResponse itemRequest =
            new ItemRequestDtoResponse(1L, "request", LocalDateTime.now(), List.of(item));
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService requestService;

    @Test
    @SneakyThrows
    void createRequest() {
        when(requestService.add(any(), any())).thenReturn(itemRequest);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequest), result);
    }

    @Test
    @SneakyThrows
    void getUserRequests() {
        when(requestService.getUserRequests(user.getId())).thenReturn(List.of(itemRequest));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequest)), result);
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        Integer from = 0;
        Integer size = 10;
        when(requestService.getAllRequests(user.getId(), from, size)).thenReturn(List.of(itemRequest));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequest)), result);
    }

    @Test
    @SneakyThrows
    void get() {
        Long requestId = 1L;

        when(requestService.getRequestById(user.getId(), requestId)).thenReturn(itemRequest);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequest), result);
    }
}
