package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse add(Long userId, ItemRequestDto requestDto);

    List<ItemRequestDtoResponse> getUserRequests(Long userId);

    List<ItemRequestDtoResponse> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoResponse getRequestById(Long userId, Long requestId);
}
