package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemRequestDtoResponse add(Long userId, ItemRequestDto requestDto) {
        User user = userService.getUserById(userId);
        if (Optional.ofNullable(requestDto.getDescription()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ItemRequest request = ItemRequestMapper.toRequest(user, requestDto);
        request.setRequestor(user);
        return ItemRequestMapper.toitemRequestDtoResponse(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDtoResponse> getUserRequests(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequestorId(userId);
        return itemRequestList.stream()
                .map(ItemRequestMapper::toitemRequestDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoResponse> getAllRequests(Long userId, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userService.getUserById(userId);
        List<ItemRequest> itemRequestList = requestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size));
        return itemRequestList.stream()
                .map(ItemRequestMapper::toitemRequestDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoResponse getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);
        Optional<ItemRequest> requestById = requestRepository.findById(requestId);
        if (requestById.isEmpty()) {
            throw new NotFoundException(String.format("Запрос с id: %s " + "не был найден.", requestId));
        }
        return ItemRequestMapper.toitemRequestDtoResponse(requestById.get());
    }
}
