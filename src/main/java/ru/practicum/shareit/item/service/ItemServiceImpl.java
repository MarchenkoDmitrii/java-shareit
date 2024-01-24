package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoOutItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;


    @Override
    @Transactional
    public List<ItemDtoOut> getAllUserItems(Long userId) {
        List<Item> itemList = itemRepository.findAllByOwner(userId);
        itemList.sort((o1, o2) -> Math.toIntExact(o1.getId() - o2.getId()));
        List<Long> idList = itemList.stream()
                .map(Item::getId)
                .collect(toList());
        Map<Long, List<CommentDtoOut>> comments = commentRepository.findAllByItemIdIn(idList).stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDtoOut::getItemId, toList()));

        Map<Long, List<BookingDtoOut>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        StatusBooking.APPROVED).stream()
                .map(BookingMapper::toBookingDtoOut)
                .collect(groupingBy(item -> item.getItem().getId(), toList()));

        return itemList
                .stream()
                .map(item -> ItemMapper.toItemDtoOut(item, getLastBooking(bookings.get(item.getId()),
                                LocalDateTime.now()), comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())))
                .collect(toList());
    }

    @Override
    @Transactional
    public ItemDtoOut getItemById(Long itemId, Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.valueOf(404)));
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
        itemDtoOut.setComments(getAllItemComments(itemId));
        if (!item.getOwner().equals(userId)) {
            return itemDtoOut;
        }
        List<Booking> bookings = bookingRepository
                .findAllByItemAndStatusOrderByStartAsc(item, StatusBooking.APPROVED);

        List<BookingDtoOut> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingDtoOut)
                .collect(toList());
        BookingDtoOutItem lastBooking = bookingDTOList.stream()
                .filter(bookingDtoOut -> !LocalDateTime.parse(bookingDtoOut.getStart())
                        .isAfter(LocalDateTime.now()))
                .max((time1, time2) -> LocalDateTime.parse(time1.getEnd(), formatter)
                        .compareTo(LocalDateTime.parse(time2.getStart(), formatter)))
                .map(BookingMapper::toBookingDtoOutItem).orElse(null);
        BookingDtoOutItem NextBooking = bookingDTOList.stream()
                .filter(bookingDtoOut -> !LocalDateTime.parse(bookingDtoOut.getStart())
                        .isBefore(LocalDateTime.now())).min((time1, time2) -> LocalDateTime.parse(time1.getEnd(), formatter)
                        .compareTo(LocalDateTime.parse(time2.getStart(), formatter)))
                .map(BookingMapper::toBookingDtoOutItem).orElse(null);
        itemDtoOut.setLastBooking(lastBooking);
        itemDtoOut.setNextBooking(NextBooking);
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
    public List<ItemDtoOut> searchItemsByText(String searchText) {
        // Логика поиска вещей по тексту в названии или описании в репозитории
        if (searchText.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDtoOut> itemDtoOut = itemRepository.search(searchText).stream()
                .map(ItemMapper::toItemDtoOut)
                .collect(toList());
        return itemDtoOut;
    }

    @Override
    @Transactional
    public CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Item itemById = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Booking> userBookings = bookingRepository
                .findAllByUserBookings(userId, itemId, LocalDateTime.now()).stream()
                .filter(state -> state.getStart().isBefore(LocalDateTime.now()))
                .collect(toList());
        if (userBookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Comment comment = CommentMapper.toComment(commentDto, itemById, user);

        if (comment.getText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return CommentMapper.toCommentDtoOut(commentRepository.save(comment));
    }

    @Transactional
    public List<CommentDtoOut> getAllItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private BookingDtoOutItem getLastBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        if (bookings.size() == 1) {
            return bookings.stream()
                    .filter(bookingDTO -> LocalDateTime.parse(bookingDTO.getEnd(), formatter).isBefore(time))
                    .map(BookingMapper::toBookingDtoOutItem)
                    .findFirst()
                    .orElse(null);
        }
        return bookings
                .stream()
                .filter(bookingDTO -> LocalDateTime.parse(bookingDTO.getEnd(), formatter).isBefore(time))
                .sorted((time1, time2) -> LocalDateTime.parse(time2.getStart(), formatter)
                        .compareTo(LocalDateTime.parse(time1.getStart(), formatter)))
                .map(BookingMapper::toBookingDtoOutItem)
                .findFirst()
                .orElse(null);
    }

    private BookingDtoOutItem getNextBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> LocalDateTime.parse(bookingDTO.getStart(), formatter).isAfter(time))
                .findFirst()
                .map(BookingMapper::toBookingDtoOutItem)
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

        userRepository.findById(idUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.valueOf(404)));

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
