package ru.isu.antlib.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="collection")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany
    @JoinTable(name="book_collection",
            joinColumns = @JoinColumn(name="collection_id"),
            inverseJoinColumns = @JoinColumn(name="user_book_mark_id")
    )
    private Set<UserBookMark> userBooks = new HashSet<>();


}
