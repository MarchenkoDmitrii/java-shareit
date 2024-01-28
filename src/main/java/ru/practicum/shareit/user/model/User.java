package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;


@Data
@NonNull
@Entity
@Table(name = "users")
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
