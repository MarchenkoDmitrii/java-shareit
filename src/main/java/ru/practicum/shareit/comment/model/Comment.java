package ru.practicum.shareit.comment.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private User authorId;

    @Column(name = "created")
    private LocalDateTime created;

}
