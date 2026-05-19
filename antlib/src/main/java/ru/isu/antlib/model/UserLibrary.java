package ru.isu.antlib.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="user_library")
public class UserLibrary {
    @EmbeddedId
    private UserLibraryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("libraryId")
    @JoinColumn(name="library_id")
    private Library library;
    @Enumerated(EnumType.STRING)
    private Role role;
}

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserLibraryId implements java.io.Serializable {
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

