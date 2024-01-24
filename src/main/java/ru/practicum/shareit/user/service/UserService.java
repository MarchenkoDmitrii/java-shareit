package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long userId);

    User saveUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
