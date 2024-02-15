package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    List<ItemDtoResponse> getAllUserItems(Long userId, Integer from, Integer size);

    ItemDtoResponse getItemById(Long itemId, Long userId);

    List<ItemDtoResponse> searchItemsByText(String searchText);

    Item findItemById(Long itemId);
}
