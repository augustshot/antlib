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
public class UserLibraryId implements java.io.Serializable {
    private Integer userId;
    private Integer libraryId;

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserLibraryId UserLibraryId = (UserLibraryId) o;
        return Objects.equals(userId, UserLibraryId.userId) && Objects.equals(libraryId, UserLibraryId.libraryId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId, libraryId);
    }
}