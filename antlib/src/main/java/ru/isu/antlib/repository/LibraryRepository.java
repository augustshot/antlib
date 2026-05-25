package ru.isu.antlib.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Library;

import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Integer> {
    boolean existsByInviteCode(String inviteCode);

    Optional<Library> findByInviteCode(String inviteCode);

    // В LibraryRepository
    @Query("SELECT l FROM Library l JOIN UserLibrary ul ON l.id = ul.library.id WHERE ul.role = 'OWNER' AND ul.user.id = :ownerId AND l.name = :name")
    Optional<Library> findByNameAndOwnerId(@Param("name") String name, @Param("ownerId") Integer ownerId);

}
