package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    List<ItemDtoOut> getAllUserItems(Long userId);

    ItemDtoOut getItemById(Long itemId, Long userId);

    List<ItemDtoOut> searchItemsByText(String searchText);

    Item findItemById(Long itemId);

}
