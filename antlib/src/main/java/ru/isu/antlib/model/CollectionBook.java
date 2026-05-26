package ru.isu.antlib.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="collection_book")
public class CollectionBook {
    @EmbeddedId
    private CollectionBookId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name="user_book_mark_id")
    private UserBookMark userBookMark;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("collectionId")
    @JoinColumn(name="collection_id")
    private Collection collection;

    public CollectionBook(Collection collection, UserBookMark userBookMark) {
        this.id = new CollectionBookId(collection.getId(), userBookMark.getId());
        this.userBookMark = userBookMark;
        this.collection = collection;
    }
}
