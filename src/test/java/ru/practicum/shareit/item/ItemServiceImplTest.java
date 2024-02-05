package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final User user = new User(1L, "username1", "email1@email.com");
    private final User user2 = new User(2L, "username2", "email2@email.com");
    private final UserDto userDto = new UserDto(1L, "username", "email@email.com");
    private final Item item =
            new Item(1L, "item1 name", "item1 description", true, user.getId(), null);
    private final ItemDto itemDto1 =
            new ItemDto(1L, "item1 name", "item1 description", true, null);
    private final ItemDto itemDto2 =
            new ItemDto(2L, "item2 name", "item2 description", true, null);
    private final BookingDto bookingDto1 =
            new BookingDto(2L, LocalDateTime.now().plusDays(1L), LocalDateTime.now().plusDays(2L));
    private final ItemRequestDto requestDto = new ItemRequestDto("request description");
    private final CommentDto commentDto = new CommentDto("comment text");
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem() {
        Item itemSaveTest
                = new Item(null, "test item name", "test description", true, user.getId(), null);

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.save(itemSaveTest)).thenReturn(itemSaveTest);

        ItemDto actualItemDto = itemService.createItem(userDto.getId(), ItemMapper.toItemDto(itemSaveTest));

        assertEquals(actualItemDto.getName(), "test item name");
        assertEquals(actualItemDto.getDescription(), "test description");
    }

    @Test
    void updateItem() {

        Item itemUpdate =
                new Item(1L, "item1 upd", "item1 upd", true, user.getId(), null);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemUpdate));

        ItemDto savedItem = itemService.updateItem(item.getId(), user.getId(), ItemMapper.toItemDto(itemUpdate));

        assertEquals("item1 upd", savedItem.getName());
        assertEquals("item1 upd", savedItem.getDescription());
    }

    @Test
    void updateItemWhenUserIsNotItemOwnerShouldThrowException() {

        Item itemUpdate =
                new Item(1L, "item1 upd", "item1 upd", true, user.getId(), null);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemUpdate));

        ResponseStatusException itemNotFoundException = assertThrows(ResponseStatusException.class,
                () -> itemService.updateItem(item.getId(), user2.getId(), ItemMapper.toItemDto(itemUpdate)));

        assertEquals(itemNotFoundException.getMessage(), HttpStatus.FORBIDDEN.toString());
    }

    @Test
    void getAllUserItems() {

    }

    @Test
    void getItemById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Item actualItemDto = itemService.findItemById(item.getId());

        assertEquals(item, actualItemDto);
    }

    @Test
    void searchItemsByText() {
        Integer from = 0;
        Integer size = 10;
        List<Item> itemList = new ArrayList<>();
        itemList.add(
                new Item(1L, "item1 name", "item1 description", true, user.getId(), null));
        itemList.add(
                new Item(2L, "item2 name", "item2 description", true, user.getId(), null));

        when(itemRepository.findAllByOwner(user.getId(), PageRequest.of(from / size, size))).thenReturn(itemList);
        when(commentService.findAllByItemIdIn(any())).thenReturn(new ArrayList<>());
        when(bookingService.findAllByItemInAndStatusOrderByStartAsc(itemList, StatusBooking.APPROVED))
                .thenReturn(new ArrayList<>());

        List<ItemDtoResponse> result = itemService.getAllUserItems(user.getId(), from, size);

        assertEquals(itemList.size(), result.size());
    }

    @Test
    public void testGetItemById() {

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentService.getAllItemComments(item.getId())).thenReturn(new ArrayList<>());
        when(bookingService.findAllByItemAndStatusOrderByStartAsc(item, StatusBooking.APPROVED)).thenReturn(new ArrayList<>());

        ItemDtoResponse result = itemService.getItemById(item.getId(), user.getId());

        assertEquals(item.getName(), result.getName());
        // Add more assertions based on your specific logic
    }

    @Test
    public void testGetItemById_ItemNotFound() {
        Long itemId = 1L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            itemService.getItemById(itemId, userId);
        });
    }
}
