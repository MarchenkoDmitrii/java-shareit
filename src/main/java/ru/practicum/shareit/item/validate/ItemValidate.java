package ru.practicum.shareit.item.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Objects;

public class ItemValidate {
    public static void validate(ItemDto item, Long idUser) {

        if (!UserServiceImpl.userStorage.containsKey(idUser)) {
            System.out.println("test");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (item.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (ItemServiceImpl.itemStorage.values().stream()
                        .anyMatch(item1 -> Objects.equals(item1.getName(), item.getName()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    public static void validate(ItemDto item, Long idUser, Long idItem) {
        // Проверка, что владелец совпадает с userId
        if (ItemServiceImpl.itemStorage.containsKey(idItem)) {
            if (!ItemServiceImpl.itemStorage.get(idItem).getOwner().getId().equals(idUser)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
    }
}
