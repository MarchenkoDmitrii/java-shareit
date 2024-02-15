package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Override
    @Transactional
    public CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userService.getUserById(userId);
        List<Booking> userBookings = bookingService
                .findAllByUserBookings(userId, itemId, LocalDateTime.now()).stream()
                .filter(state -> state.getStart().isBefore(LocalDateTime.now()))
                .collect(toList());
        if (userBookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Comment comment = CommentMapper.toComment(commentDto, itemId, user);

        if (comment.getText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return CommentMapper.toCommentDtoOut(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);

    }

    @Override
    @Transactional
    public List<CommentDtoOut> getAllItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList());
    }

    @Override
    public List<Comment> findAllByItemIdIn(List<Long> itemIds) {
        return commentRepository.findAllByItemIdIn(itemIds);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findAllByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }
}
