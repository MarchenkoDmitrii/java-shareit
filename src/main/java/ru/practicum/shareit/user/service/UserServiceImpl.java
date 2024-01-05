package ru.practicum.shareit.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import java.util.*;

@Component
public class UserServiceImpl {
    public Long idUser = 0L;
    public static final Map<Long, User> userStorage = new HashMap<>();

    public List<User> getAllUserItems() {
        return new ArrayList<>(userStorage.values());
    }

    public User getUserById(Long id) {
        return Optional.ofNullable(userStorage.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Нет такого пользователя"));
    }

    public User getUserByName(String name) {
        return  userStorage.values().stream()
                .filter(item -> Objects.equals(item.getName(), name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Нет такого пользователя"));
    }

    public User createUser(UserDto user) {
        idUser++;
        User put = UserMapper.toUser(user, idUser);
        userStorage.put(idUser, put);
        return put;
    }


    public User updateUser(Long userId, UserDto user) {
        if (user.getName() != null) {
            userStorage.get(userId).setName(user.getName());
        }

        if (user.getEmail() != null) {
            userStorage.get(userId).setEmail(user.getEmail());
        }
        return userStorage.get(userId);
    }

    public void deleteUserById(Long userId) {
        if (!userStorage.containsKey(userId)) {
            // Если пользователя с указанным ID нет, вы можете выбрасывать исключение или выполнять другую логику
            // Здесь выбрасываем исключение ResponseStatusException с кодом 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
        }
        // Удаление пользователя по ID
        userStorage.remove(userId);
    }

}
