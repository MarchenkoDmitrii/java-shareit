package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoResponseForItems;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null
        );
    }

    public static ItemDtoResponse toItemDtoResponse(Item item) {
        ItemDtoResponse itemDtoOut = new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        itemDtoOut.setOwnerId(item.getOwner());
        itemDtoOut.setRequestId(item.getRequestId() != null ? item.getRequestId() : null);
        return itemDtoOut;
    }

    public static ItemDtoResponse toItemDtoResponse(Item item, BookingDtoResponseForItems lastBooking, List<CommentDtoOut> comments, BookingDtoResponseForItems nextBooking) {
        ItemDtoResponse itemDtoOut = new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        itemDtoOut.setLastBooking(lastBooking);
        itemDtoOut.setComments(comments);
        itemDtoOut.setNextBooking(nextBooking);
        return itemDtoOut;
    }

    public static Item toItem(ItemDto itemDto, Long userId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(userId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }
}
