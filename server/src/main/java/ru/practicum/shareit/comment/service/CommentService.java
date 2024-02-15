package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId);

    void deleteComment(Long commentId);

    List<CommentDtoOut> getAllItemComments(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);

    Comment save(Comment comment);

    List<Comment> findAllByItemId(Long itemId);
}
