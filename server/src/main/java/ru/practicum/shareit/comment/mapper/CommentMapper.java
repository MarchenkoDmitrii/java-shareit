package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoOut;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getText());
    }

    public static CommentDtoOut toCommentDtoOut(Comment comment) {
        return new CommentDtoOut(
                comment.getId(),
                comment.getText(),
                comment.getAuthorId().getName(),
                convertLocalDateTimeToString(comment.getCreated()),
                comment.getItemId());
    }

    public static Comment toComment(CommentDto commentDto, long itemId, User user) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthorId(user);
        comment.setItemId(itemId);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    private static String convertLocalDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.plusMinutes(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : "undefined";
    }
}
