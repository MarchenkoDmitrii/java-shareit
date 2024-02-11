package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Create item with userId={}, itemDto={}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable("itemId") long itemId) {
        log.info("Update item with userId={}, itemId={} itemDto={}", userId, itemId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId) {
        log.info("Get item with itemId={}", itemId);
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET all items with userId: {}", userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "text") String text,
                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET search items with same text: {}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable long itemId) {
        log.info("Create comment with userId={}, commentDto={}", userId, commentDto);
        return itemClient.createComment(userId, commentDto, itemId);
    }

}
