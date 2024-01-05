package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserServiceImpl;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto, Long userId, Long itemId) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                UserServiceImpl.userStorage.get(userId),
                itemDto.getRequest());
    }

}
