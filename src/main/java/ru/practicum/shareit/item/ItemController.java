package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validate.ItemValidate;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    @Autowired
    ItemService itemService;

    @PostMapping
    public ResponseEntity<Item> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemDto itemDto) {
        if (userId == null) {
            ResponseEntity.status(400).build();
        }
        ItemValidate.validate(itemDto,userId);
        Item item = itemService.createItem(itemDto, userId);
        return ResponseEntity.status(200).body(item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemUpdateRequest) {
        if (userId == null) {
            return ResponseEntity.status(500).build();
        }
        ItemValidate.validate(itemUpdateRequest, userId, itemId);
        // Логика обновления вещи
        Item updateItem = itemService.updateItem(itemId, itemUpdateRequest);
        return ResponseEntity.status(200).body(updateItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable Long itemId) {
        // Логика получения информации о вещи по её идентификатору
        Item item = itemService.getItemById(itemId);

        if (item != null) {
            return ResponseEntity.status(200).body(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Item>> getItemsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        // Логика получения списка вещей для владельца
        List<Item> items = itemService.allUsersItems(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam("text") String searchText) {
        // Логика поиска вещей по тексту в названии или описании
        List<Item> foundItems = itemService.searchItems(searchText);
        return ResponseEntity.ok(foundItems);
    }
}
