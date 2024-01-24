package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoOutItem;
import ru.practicum.shareit.comment.dto.CommentDtoOut;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDtoOut {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Boolean available;
    private BookingDtoOutItem lastBooking;
    private List<CommentDtoOut> comments;
    private BookingDtoOutItem nextBooking;
    private Long requestId;

}
