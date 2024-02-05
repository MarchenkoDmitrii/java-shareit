package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {
    private final UserDto userDto1 = new UserDto(null, "username1", "email1@email.com");
    private final UserDto userDto2 = new UserDto(null, "username2", "email2@email.com");
    private final ItemDto itemDto1 =
            new ItemDto(null, "item1 name", "item1 description", true, null);
    private final ItemDto itemDto2 =
            new ItemDto(null, "item2 name", "item2 description", true, null);
    private final BookingDto bookingDto1 =
            new BookingDto(2L, LocalDateTime.now().plusDays(1L), LocalDateTime.now().plusDays(2L));
    private final ItemRequestDto requestDto = new ItemRequestDto("request description");
    private final CommentDto commentDto = new CommentDto("comment text");
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private BookingService bookingService;

    @Test
    @SneakyThrows
    void addCommentItem() {
        User addedUser1 = userService.saveUser(userDto1);
        User addedUser2 = userService.saveUser(userDto2);
        ItemDto addedItem = itemService.createItem(addedUser2.getId(), itemDto2);
        BookingDtoResponse bookingDtoOut = bookingService.add(addedUser1.getId(), bookingDto1);

        bookingService.update(addedUser2.getId(), bookingDtoOut.getId(), true);
        Thread.sleep(2000);
        CommentDtoOut addedComment = commentService.createComment(addedUser1.getId(), commentDto, addedItem.getId());

        assertEquals(1L, addedComment.getId());
        assertEquals("comment text", addedComment.getText());
    }

    @Test
    void addNewItem() {
        User addedUser = userService.saveUser(userDto1);
        ItemDto addedItem = itemService.createItem(addedUser.getId(), itemDto1);

        assertEquals(1L, addedItem.getId());
        assertEquals("item1 name", addedItem.getName());
    }

    @Test
    void addRequestItem() {
        User addedUser = userService.saveUser(userDto1);
        requestService.add(addedUser.getId(), requestDto);

        ItemDto addedItemRequest = itemService.createItem(addedUser.getId(),
                new ItemDto(1L, "itemDtoRequest name", "desc", true, null));

        assertEquals(1L, addedItemRequest.getRequestId());
        assertEquals("itemDtoRequest name", addedItemRequest.getName());
    }

    @Test
    void getItemByIdWhenItemIdIsNotValid() {
        Long itemId = 3L;

        Assertions
                .assertThrows(RuntimeException.class,
                        () -> itemService.findItemById(itemId));
    }
}
