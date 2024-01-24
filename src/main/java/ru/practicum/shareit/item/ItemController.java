package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    @Autowired
    ItemService itemService;

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
    public ResponseEntity<ItemDtoOut> getItemById(@PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        // Логика получения информации о вещи по её идентификатору
        ItemDtoOut item = itemService.getItemById(itemId, userId);

        if (item != null) {
            return ResponseEntity.status(200).body(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoOut>> getItemsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        // Логика получения списка вещей для владельца
        List<ItemDtoOut> items = itemService.getAllUserItems(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoOut>> searchItems(@RequestParam("text") String searchText) {
        // Логика поиска вещей по тексту в названии или описании
        List<ItemDtoOut> foundItems = itemService.searchItemsByText(searchText);
        return ResponseEntity.ok(foundItems);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Validated @RequestBody CommentDto commentDto,
                                       @PathVariable Long itemId) {
        return itemService.createComment(userId, commentDto, itemId);
    }
}
