package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItems;
import ru.practicum.shareit.comment.dto.CommentDtoOut;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDtoResponse {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Boolean available;
    private BookingDtoResponseForItems lastBooking;
    private List<CommentDtoOut> comments;
    private BookingDtoResponseForItems nextBooking;
    private Long requestId;

}
