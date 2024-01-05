package ru.practicum.shareit.user.dto;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NonNull
public class UserDto {
    private String name;
    private String email;
}
