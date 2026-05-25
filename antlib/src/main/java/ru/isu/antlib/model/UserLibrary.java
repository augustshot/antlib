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

    public UserLibrary(User user, Library library, Role role) {
        this.user = user;
        this.library = library;
        this.role = role;
        this.id = new UserLibraryId(user.getId(), library.getId());
    }

    @Override
    public String toString() {
        return "UserLibrary{" +
                "id=" + id +
                ", user=" + user +
                ", library=" + library +
                ", role=" + role +
                '}';
    }
}



