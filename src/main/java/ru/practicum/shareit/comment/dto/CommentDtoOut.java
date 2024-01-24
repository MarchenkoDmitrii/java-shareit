package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDtoOut {
    private Long id;
    private String text;
    private String authorName;
    private String created;
    private Long itemId;
}
