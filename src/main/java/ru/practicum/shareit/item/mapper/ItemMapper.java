package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoOutItem;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static ItemDtoOut toItemDtoOut(Item item) {
        ItemDtoOut itemDtoOut = new ItemDtoOut(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        itemDtoOut.setRequestId(item.getRequest() != null ? item.getRequest() : null);
        return itemDtoOut;
    }

    public static ItemDtoOut toItemDtoOut(Item item, BookingDtoOutItem lastBooking, List<CommentDtoOut> comments, BookingDtoOutItem nextBooking) {
        ItemDtoOut itemDtoOut = new ItemDtoOut(
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
        item.setRequest(item.getRequest() != null ? item.getRequest() : null);
        return item;
    }
}
