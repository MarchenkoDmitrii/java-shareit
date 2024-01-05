package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {


    private final ItemServiceImpl itemServiceImpl;

    public List<Item> allUsersItems(Long id) {
       return itemServiceImpl.getAllUserItems(id);
    }

    public Item getItemById(Long id) {
        return itemServiceImpl.getItemById(id);
    }

    public Item getItemByName(String name) {
        return itemServiceImpl.getItemByName(name);
    }

    public Item createItem(ItemDto item, Long id) {

      return itemServiceImpl.createItem(item, id);
    }

    public Item updateItem(Long itemId, ItemDto itemDto) {
       return itemServiceImpl.updateItem(itemId, itemDto);
    }

    public List<Item> searchItems(String searchText) {
        // Логика поиска вещей по тексту в названии или описании в репозитории
        return itemServiceImpl.searchItemsByText(searchText);
    }

    public void deleteItem(Item item) {
    }
}
