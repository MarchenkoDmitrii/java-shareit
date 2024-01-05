package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceImpl userService;


    public List<User> allUsers() {
        return userService.getAllUserItems();
    }

    public User getUserById(long id) {
        return userService.getUserById(id);
    }

    public User createUser(UserDto user) {
        return userService.createUser(user);
    }

    public User updateUser(Long idUser, UserDto user) {
        return userService.updateUser(idUser,user);
    }

    public void deleteUser(Long idUser) {
       userService.deleteUserById(idUser);
    }
}
