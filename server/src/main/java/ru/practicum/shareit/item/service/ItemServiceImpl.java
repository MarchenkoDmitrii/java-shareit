package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoResponseForItems;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final CommentService commentService;

    @Override
    @Transactional
    public List<ItemDtoResponse> getAllUserItems(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> itemList = itemRepository.findAllByOwner(userId, pageable);
        itemList.sort((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()));
        List<Long> idList = itemList.stream()
                .map(Item::getId)
                .collect(toList());
        Map<Long, List<CommentDtoOut>> comments = commentService.findAllByItemIdIn(idList).stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDtoOut::getItemId, toList()));

        Map<Long, List<BookingDtoResponse>> bookings = bookingService.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        StatusBooking.APPROVED).stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(groupingBy(item -> item.getItem().getId(), toList()));

        return itemList
                .stream()
                .map(item -> ItemMapper.toItemDtoResponse(item, getLastBooking(bookings.get(item.getId()),
                                LocalDateTime.now()), comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())))
                .collect(toList());
    }

    @Override
    @Transactional
    public ItemDtoResponse getItemById(Long itemId, Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.valueOf(404)));
        ItemDtoResponse itemDtoOut = ItemMapper.toItemDtoResponse(item);

        if (Optional.ofNullable(commentService.getAllItemComments(itemId)).isPresent()) {
            itemDtoOut.setComments(commentService.getAllItemComments(itemId));
        }
        if (!item.getOwner().equals(userId)) {
            return itemDtoOut;
        }

        List<Booking> bookings = bookingService
                .findAllByItemAndStatusOrderByStartAsc(item, StatusBooking.APPROVED);

        List<BookingDtoResponse> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(toList());
        BookingDtoResponseForItems lastBooking = bookingDTOList.stream()
                .filter(bookingDtoOut -> !LocalDateTime.parse(bookingDtoOut.getStart())
                        .isAfter(LocalDateTime.now()))
                .max((time1, time2) -> LocalDateTime.parse(time1.getEnd(), formatter)
                        .compareTo(LocalDateTime.parse(time2.getStart(), formatter)))
                .map(BookingMapper::toBookingResponseForItems).orElse(null);

        BookingDtoResponseForItems nextBooking = bookingDTOList.stream()
                .filter(bookingDtoOut -> !LocalDateTime.parse(bookingDtoOut.getStart())
                        .isBefore(LocalDateTime.now())).min((time1, time2) -> LocalDateTime.parse(time1.getEnd(), formatter)
                        .compareTo(LocalDateTime.parse(time2.getStart(), formatter)))
                .map(BookingMapper::toBookingResponseForItems).orElse(null);
        itemDtoOut.setLastBooking(lastBooking);
        itemDtoOut.setNextBooking(nextBooking);
        return itemDtoOut;
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        validate(itemDto, userId);
        Item item = ItemMapper.toItem(itemDto, userId);
        return ItemMapper.toItemDto(itemRepository.save(item));

    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        validate(itemId, userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public List<ItemDtoResponse> searchItemsByText(String searchText) {
        // Логика поиска вещей по тексту в названии или описании в репозитории
        if (searchText.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(searchText).stream()
                .map(ItemMapper::toItemDtoResponse)
                .collect(toList());
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    private BookingDtoResponseForItems getLastBooking(List<BookingDtoResponse> bookings, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        if (bookings.size() == 1) {
            return bookings.stream()
                    .filter(bookingDTO -> LocalDateTime.parse(bookingDTO.getEnd(), formatter).isBefore(time))
                    .map(BookingMapper::toBookingResponseForItems)
                    .findFirst()
                    .orElse(null);
        }
        return bookings
                .stream()
                .filter(bookingDTO -> LocalDateTime.parse(bookingDTO.getEnd(), formatter).isBefore(time))
                .sorted((time1, time2) -> LocalDateTime.parse(time2.getStart(), formatter)
                        .compareTo(LocalDateTime.parse(time1.getStart(), formatter)))
                .map(BookingMapper::toBookingResponseForItems)
                .findFirst()
                .orElse(null);
    }

    private BookingDtoResponseForItems getNextBooking(List<BookingDtoResponse> bookings, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> LocalDateTime.parse(bookingDTO.getStart(), formatter).isAfter(time))
                .findFirst()
                .map(BookingMapper::toBookingResponseForItems)
                .orElse(null);
    }

    public void validate(ItemDto item, Long idUser) {

        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.valueOf(400));
        }

        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.valueOf(400));
        }

        if (item.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.valueOf(400));
        }
        if (Optional.ofNullable(userService.getUserById(idUser)).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.valueOf(404));
        }


        if (itemRepository.findAll().stream()
                .anyMatch(item1 -> Objects.equals(item1.getName(), item.getName()))) {
            throw new ResponseStatusException(HttpStatus.valueOf(400));
        }
    }

    public void validate(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.valueOf(404)));

        // Проверка, что владелец совпадает с userId
        if (!item.getOwner().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.valueOf(403));
        }
    }

}
