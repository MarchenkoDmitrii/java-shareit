package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NonNull
@Entity
@Table(name = "requests")
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;

    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items = new ArrayList<>();
}
