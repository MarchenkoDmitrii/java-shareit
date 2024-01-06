package ru.practicum.shareit.user.model;

import lombok.*;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NonNull
public class User {
   private Long id;
   private String name;
   private String email;
}
