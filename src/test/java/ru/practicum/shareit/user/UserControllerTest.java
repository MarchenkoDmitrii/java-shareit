package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void createUserWhenUserIsValid() {
        User user = new User(1L, "username", "email@email.com");

        when(userService.saveUser(UserMapper.toUserDto(user))).thenReturn(user);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @Test
    @SneakyThrows
    void updateUserWhenUserIsValid() {
        Long userId = 0L;
        User user = new User(1L, "username", "email@email.com");

        when(userService.updateUser(userId, UserMapper.toUserDto(user))).thenReturn(user);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @Test
    @SneakyThrows
    void get() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("testuser");

        // Mocking the userService behavior
        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("testuser"));
    }

    @Test
    @SneakyThrows
    void findAll() {
        List<User> usersDtoToExpect = List.of(new User(1L, "username", "email@email.com"));

        when(userService.getAllUsers()).thenReturn(usersDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(usersDtoToExpect), result);
    }

    @Test
    @SneakyThrows
    void delete() {
        long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}
