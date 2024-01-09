package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NonNull
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}
