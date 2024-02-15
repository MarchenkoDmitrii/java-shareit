package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getDescription()
        );
    }

    public static ItemRequest toRequest(User user, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return itemRequest;
    }

    public static ItemRequestDtoResponse toitemRequestDtoResponse(ItemRequest request) {
        List<ItemDtoResponse> itemRequestDtoResponse = new ArrayList<>();
        if (!Objects.isNull(request.getItems())) {
            itemRequestDtoResponse = request.getItems().stream()
                    .map(ItemMapper::toItemDtoResponse)
                    .collect(Collectors.toList());
        }
        return new ItemRequestDtoResponse(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemRequestDtoResponse);
    }
}
