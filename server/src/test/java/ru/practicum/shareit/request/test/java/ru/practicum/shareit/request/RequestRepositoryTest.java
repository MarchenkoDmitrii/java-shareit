package ru.practicum.shareit.request.test.java.ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestorIdOrderByCreatedAscTest() {
        User user = userRepository.save(new User(1L, "username", "email@email.com"));
        itemRequestRepository.save(new ItemRequest(1L, "eeqwe", user, LocalDateTime.now(), null));
        List<ItemRequest> items = itemRequestRepository.findAllByRequestorId(user.getId());
        assertEquals(items.size(), 1);
    }

}
