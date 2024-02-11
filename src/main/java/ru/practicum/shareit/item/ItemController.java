package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemDto itemDto) {
        if (userId == null) {
            ResponseEntity.status(400).build();
        }
        ItemDto item = itemService.createItem(userId, itemDto);
        return ResponseEntity.status(200).body(item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemDto itemUpdateRequest) {
        if (userId == null) {
            return ResponseEntity.status(500).build();
        }
        // Логика обновления вещи
        ItemDto updateItem = itemService.updateItem(itemId, userId, itemUpdateRequest);
        return ResponseEntity.status(200).body(updateItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> getItemById(@PathVariable Long itemId,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        // Логика получения информации о вещи по её идентификатору
        ItemDtoResponse item = itemService.getItemById(itemId, userId);

        if (item != null) {
            return ResponseEntity.status(200).body(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoResponse>> getItemsForOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        // Логика получения списка вещей для владельца
        List<ItemDtoResponse> items = itemService.getAllUserItems(userId, from, size);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoResponse>> searchItems(@RequestParam("text") String searchText) {
        // Логика поиска вещей по тексту в названии или описании
        List<ItemDtoResponse> foundItems = itemService.searchItemsByText(searchText);
        return ResponseEntity.ok(foundItems);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Validated @RequestBody CommentDto commentDto,
                                       @PathVariable Long itemId) {
        return commentService.createComment(userId, commentDto, itemId);
    }
}
