package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NonNull
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private Instant created;
}
