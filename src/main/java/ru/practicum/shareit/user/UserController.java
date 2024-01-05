package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.validate.UserValidate;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody UserDto userDto) {
        UserValidate.userValidate(userDto);
        User user = userService.createUser(userDto);
        return ResponseEntity.status(200).body(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId,
                           @RequestBody UserDto userDto) {
        UserValidate.userValidate(userDto, userId);
        // Логика обновления вещи
         userService.updateUser(userId, userDto);
        return ResponseEntity.ok().body(UserServiceImpl.userStorageUnmod.get(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        // Логика получения информации о вещи по её идентификатору
        User user = userService.getUserById(userId);

        if (user != null) {
            return ResponseEntity.status(200).body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // Логика получения информации о вещи по её идентификатору
        List<User> userDto = userService.allUsers();

        if (userDto != null) {
            return ResponseEntity.status(200).body(userDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
