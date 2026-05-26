package ru.isu.antlib.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionBookId implements java.io.Serializable {
    private Integer collectionId;
    private Integer bookId;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CollectionBookId that = (CollectionBookId) o;
        return Objects.equals(collectionId, that.collectionId) && Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, bookId);
    }
}