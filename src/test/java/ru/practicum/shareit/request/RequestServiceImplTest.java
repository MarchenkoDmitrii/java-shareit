package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Test
    public void testAddItemRequest() {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test description");

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        when(userService.getUserById(userId)).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDtoResponse result = requestService.add(userId, requestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    public void testGetUserRequests() {
        // Mock data
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        List<ItemRequest> itemRequestList = new ArrayList<>();

        when(userService.getUserById(userId)).thenReturn(user);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(itemRequestList);

        List<ItemRequestDtoResponse> result = requestService.getUserRequests(userId);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetAllRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        when(userService.getUserById(userId)).thenReturn(new User());

        List<ItemRequest> itemRequestList = new ArrayList<>();
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size)))
                .thenReturn(itemRequestList);

        List<ItemRequestDtoResponse> result = requestService.getAllRequests(userId, from, size);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetRequestById() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userService.getUserById(userId)).thenReturn(new User());

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDtoResponse result = requestService.getRequestById(userId, requestId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemRequest.getId(), result.getId());
    }

}
