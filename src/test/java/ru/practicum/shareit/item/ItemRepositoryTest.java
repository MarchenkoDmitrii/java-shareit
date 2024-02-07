package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
public class ItemRepositoryTest {
    User user;
    Item item;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void addItems() {
        user = userRepository.save(new User(1L, "username", "email@email.com"));
        item = itemRepository.save(new Item(1L, "item name", "description", true, user.getId(), null));
    }

    @AfterEach
    void deleteAll() {
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerOrderByIdAsc(user.getId(), PageRequest.of(0, 1));

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "item name");
    }
}
