package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class BookingDtoResponse {
    private Long id;
    private ItemDtoResponse item;
    private String start;
    private String end;
    private UserDto booker;
    private StatusBooking status;

}
