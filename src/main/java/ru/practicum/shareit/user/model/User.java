package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NonNull
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;
}
