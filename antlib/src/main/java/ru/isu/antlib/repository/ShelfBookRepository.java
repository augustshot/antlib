package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.ShelfBook;

import java.util.List;
import java.util.Map;

@Repository
public interface ShelfBookRepository extends JpaRepository<ShelfBook, Integer> {
    List<ShelfBook> findByShelfId(Integer shelfId);


    @Query("SELECT COUNT(sb) FROM ShelfBook sb WHERE sb.shelf.id = :shelfId")
    int countByShelfId(@Param("shelfId") Integer shelfId);

    @Query("SELECT " +
            "sb.id as shelfBookId, " +
            "bd.id as bookDescriptionId, " +
            "bd.title as title, " +
            "bd.author as author, " +
            "bd.ISBN as isbn, " +
            "s.id as shelfId, " +
            "r.id as roomId, " +
            "r.name as roomName " +
            "FROM ShelfBook sb " +
            "JOIN sb.bookDescription bd " +
            "JOIN sb.shelf s " +
            "JOIN s.room r " +
            "WHERE r.library.id = :libraryId " +
            "AND (LOWER(bd.title) LIKE :search " +
            "OR LOWER(bd.author) LIKE :search " +
            "OR bd.ISBN LIKE :search)")
    List<Map<String, Object>> searchBooksInLibrary(@Param("libraryId") Integer libraryId,
                                                   @Param("search") String search);

    
}
