package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    private final User user = new User(1L, "username", "email@email.com");
    private final Item item =
            new Item(1L, "item name", "description", true, user.getId(), null);
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    private void addItems() {
        testEntityManager.persist(user);
        testEntityManager.flush();
        itemRepository.save(item);
    }

    @AfterEach
    private void deleteAll() {
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerOrderByIdAsc(1L, PageRequest.of(0, 1));

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "name");
    }
}
