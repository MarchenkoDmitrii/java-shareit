package ru.practicum.shareit.user.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

public class UserValidate {

    public static void userValidate(UserDto user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is null");
        }

        // Валидация имени
        if ((user.getName() == null || user.getName().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be null or empty");
        }

        // Валидация email
        if (user.getEmail() != null) {
            // Валидация формата email при наличии значения
            if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
            }
            // Проверка на дубликат email при создании нового пользователя
            if (UserServiceImpl.userStorage.values().stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
                throw new ResponseStatusException(HttpStatus.valueOf(409), "Email already exists");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Email cannot be empty");
        }
    }

    public static void userValidate(UserDto user, long idUser) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is null");
        }
        // Валидация email
        if (user.getEmail() != null) {
            // Валидация формата email при наличии значения
            if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
            }
            // Проверка на дубликат email при обновлении пользователя
            if (UserServiceImpl.userStorage.values().stream()
                    .filter(user1 -> !user1.getId().equals(idUser))
                    .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
                throw new ResponseStatusException(HttpStatus.valueOf(409), "Email already exists");
            }
        }
    }
}
