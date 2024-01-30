package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Data
@NonNull
@Entity
@Table(name = "requests")
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "requestor_id")
    private Long requestor;

    private Instant created;
}
