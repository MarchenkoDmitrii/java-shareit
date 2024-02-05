package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDtoResponse add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemRequestDto requestDto) {
        return requestService.add(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
