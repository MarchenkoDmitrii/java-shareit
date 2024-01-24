package ru.practicum.shareit.request.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NonNull
@Entity
@Table(name = "requests")
@Getter
@Setter
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
