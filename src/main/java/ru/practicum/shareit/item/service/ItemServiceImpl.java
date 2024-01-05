package ru.practicum.shareit.item.service;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemServiceImpl {
    public Long idItem = 0L;
    public static final HashMap<Long, Item> itemStorage = new HashMap<>();

    public List<Item> getAllUserItems(Long id) {
        return itemStorage.values().stream()
                .filter(owner -> owner.getOwner().equals(UserServiceImpl.userStorage.get(id)))
                .collect(Collectors.toList());
    }

    public Item getItemById(Long id) {
        return Optional.ofNullable(itemStorage.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Нет такой вещи"));
    }

    public Item getItemByName(String name) {
        return  itemStorage.values().stream()
                .filter(item -> Objects.equals(item.getName(), name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Нет такой вещи"));
    }

    public Item createItem(ItemDto itemDto, Long userId) {
        idItem++;
        Item item = ItemMapper.toItem(itemDto, userId, idItem);
        itemStorage.put(idItem, item );
        return item;
    }

    public Item updateItem(Long itemId, ItemDto itemDto) {

        if (itemDto.getName() != null) {
            itemStorage.get(itemId).setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemStorage.get(itemId).setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemStorage.get(itemId).setAvailable(itemDto.getAvailable());
        }
        return itemStorage.get(itemId);
    }

    public List<Item> searchItemsByText(String searchText) {
        // Логика поиска вещей по тексту в названии или описании в репозитории
        if (searchText.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Item> itemsName = itemStorage.values().stream()
               .filter(name -> name.getName().toLowerCase().contains(searchText.toLowerCase()))
               .collect(Collectors.toSet());
        Set<Item> itemsDesc = itemStorage.values().stream()
               .filter(desc -> desc.getDescription().toLowerCase().contains(searchText.toLowerCase()))
               .collect(Collectors.toSet());
        itemsName.addAll(itemsDesc);
        return itemsName.stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
