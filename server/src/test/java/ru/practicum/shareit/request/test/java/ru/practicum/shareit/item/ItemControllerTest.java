package ru.practicum.shareit.request.test.java.ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    private final User user = new User(1L, "username", "email@email.com");
    private final Item item =
            new Item(1L, "item name", "description", false, user.getId(), null);
    private final ItemDto itemDto =
            new ItemDto(1L, "item name", "description", false, user.getId());
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    @Test
    @SneakyThrows
    void createItemWhenItemIsValid() {
        Long userId = 1L;
        ItemDto itemDtoToCreate =
                new ItemDto(2L, "item name 2", "description 2", false, user.getId());

        when(itemService.createItem(userId, itemDtoToCreate)).thenReturn(itemDtoToCreate);

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }


    @Test
    @SneakyThrows
    void updateWhenItemIsValidShouldReturnStatusIsOk() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDtoToCreate =
                new ItemDto(itemId, "item name 2", "description 2", false, null);

        when(itemService.updateItem(userId, itemId, itemDtoToCreate)).thenReturn(itemDtoToCreate);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void getShouldReturnStatusOk() {
        Long userId = 0L;
        ItemDto itemDtoToCreate =
                new ItemDto(null, "item name 2", "description 2", false, user.getId());

        when(itemService.createItem(userId, itemDtoToCreate)).thenReturn(itemDtoToCreate);

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void getAllShouldReturnStatusOk() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemDtoResponse> itemsDtoToExpect = List.of(
                new ItemDtoResponse(1L, "item name", "description",
                        false, user.getId(), null, null, null, null));

        when(itemService.getAllUserItems(userId, from, size)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }

    @Test
    @SneakyThrows
    void searchItemsShouldReturnStatusOk() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        String text = "some";

        List<ItemDtoResponse> itemsDtoToExpect = List.of(
                new ItemDtoResponse(1L, "some item name", "some item description", true));

        when(itemService.searchItemsByText(text)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }


    @Test
    @SneakyThrows
    void createCommentWhenCommentIsValidShouldReturnStatusIsOk() {

        CommentDto commentToAdd = new CommentDto("some comment");
        CommentDtoOut commentDtoOut =
                new CommentDtoOut(1L, commentToAdd.getText(), user.getName(), LocalDateTime.now().toString(), item.getId());
        when(commentService.createComment(user.getId(), commentToAdd, item.getId())).thenReturn(commentDtoOut);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(commentToAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDtoOut), result);
    }
}
