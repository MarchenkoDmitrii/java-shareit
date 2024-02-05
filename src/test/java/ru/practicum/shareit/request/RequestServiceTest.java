package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceTest {
    private final UserDto user = new UserDto(1L, "username1", "email1@email.com");
    private final ItemRequestDto itemRequest =
            new ItemRequestDto("request description");
    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserService userService;

    @Test
    void addNewRequest() {
        User addedUser = userService.saveUser(user);
        requestService.add(addedUser.getId(), itemRequest);

        List<ItemRequestDtoResponse> actualRequests = requestService.getUserRequests(addedUser.getId());

        assertEquals("request description", actualRequests.get(0).getDescription());
    }

    @Test
    void getRequestByIdWhenRequestIdIsNotValidShouldThrowObjectNotFoundException() {
        Long requestId = 2L;

        Assertions
                .assertThrows(RuntimeException.class,
                        () -> requestService.getRequestById(user.getId(), requestId));
    }
}
