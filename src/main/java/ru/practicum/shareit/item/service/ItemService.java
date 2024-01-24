package ru.practicum.shareit.item.service;


import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;


public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    List<ItemDtoOut> getAllUserItems(Long userId);

    ItemDtoOut getItemById(Long itemId, Long userId);

    List<ItemDtoOut> searchItemsByText(String searchText);

    CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId);

    void deleteComment(Long commentId);

}
