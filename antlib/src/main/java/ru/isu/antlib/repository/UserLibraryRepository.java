package ru.isu.antlib.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Library;
import ru.isu.antlib.model.User;
import ru.isu.antlib.model.UserLibrary;
import ru.isu.antlib.model.UserLibraryId;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLibraryRepository extends JpaRepository<UserLibrary, UserLibraryId> {

    @Query("SELECT ul FROM UserLibrary ul JOIN fetch Library l ON l.id = ul.library.id WHERE ul.user.id = :userId")
    List<UserLibrary> findAllByUserId(@Param("userId") Integer userId);

    @Query("SELECT ul.user FROM UserLibrary ul WHERE ul.library.id = :libraryId AND ul.role = 'OWNER'")
    User findOwnerByLibraryId(@Param("libraryId") Integer libraryId); // !!!

    @Query("SELECT ul FROM UserLibrary ul JOIN FETCH ul.user WHERE ul.library.id = :libraryId")
    List<UserLibrary> findMembersByLibraryId(@Param("libraryId") Integer libraryId);

    boolean existsByUserIdAndLibraryId(Integer userId, Integer libraryId);

    UserLibrary findByUserIdAndLibraryId(Integer userId, Integer libraryId);

    Integer countByLibraryId(Integer libraryId);

}
