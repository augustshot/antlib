package ru.isu.antlib.repository;

import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.DialectOverride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Source;
import ru.isu.antlib.model.Status;
import ru.isu.antlib.model.User;
import ru.isu.antlib.model.UserBookMark;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookMarkRepository extends JpaRepository<UserBookMark, Integer>, JpaSpecificationExecutor<UserBookMark> {

    @Query("select ubm from UserBookMark ubm join fetch ubm.bookDescription where ubm.id = :id")
    UserBookMark findBookByUserBookMarkId(Integer id);

    List<UserBookMark> findAllByUserId(Integer userId);


    @Query("select ubm from UserBookMark ubm join fetch ubm.bookDescription where ubm.user.id = :userId and ubm.bookDescription.ISBN = :ISBN")
    UserBookMark findBookByUserIdAndISBN(@Param("userId") Integer userId, @Param("ISBN") String isbn);


    @Query("SELECT ubm FROM UserBookMark ubm WHERE ubm.user.id = :userId AND ubm.bookDescription.id = :bookId")
    Optional<UserBookMark> findByUserIdAndBookDescriptionId(@Param("userId") Integer userId,
                                                            @Param("bookId") Integer bookId);

    @Query("SELECT ubm FROM UserBookMark ubm JOIN fetch ubm.bookDescription bd " +
            "WHERE ubm.user.id = :userId AND ubm.source = :source " +
            "AND (LOWER(bd.title) LIKE :search OR LOWER(bd.author) LIKE :search OR bd.ISBN LIKE :search)")
    List<UserBookMark> findByUserIdAndSourceAndSearch(@Param("userId") Integer userId,
                                                      @Param("source") Source source,
                                                      @Param("search") String search);

    List<UserBookMark> findByUserIdAndSource(Integer userId, Source source);

    @Query("SELECT DISTINCT ubm FROM UserBookMark ubm " +
            "JOIN FETCH ubm.bookDescription bd " +
            "WHERE ubm.user.id = :userId " +
            "AND (LOWER(bd.title) LIKE LOWER(:search) " +
            "OR LOWER(bd.author) LIKE LOWER(:search) " +
            "OR bd.ISBN LIKE :search)")
    List<UserBookMark> findDistinctByUserIdAndSearch(@Param("userId") Integer userId,
                                               @Param("search") String search);


    // статистика
    int countByUserIdAndStatus(Integer userId, Status status);

    @Query("SELECT COUNT(ubm) FROM UserBookMark ubm WHERE ubm.user.id = :userId AND ubm.status IS NULL")
    int countByUserIdAndNoStatus(@Param("userId") Integer userId);

    int countByUserIdAndSource(Integer userId, Source source);

    @Query("SELECT COUNT(ubm) FROM UserBookMark ubm WHERE ubm.user.id = :userId AND ubm.source IS NULL")
    Integer countByUserIdAndNoSource(@Param("userId") Integer userId);

    @Query("SELECT SUM(bd.pages) FROM UserBookMark ubm JOIN ubm.bookDescription bd WHERE ubm.user.id = :userId AND ubm.status = 'FINISHED'")
    Integer getTotalPagesByUserIdAndFinished(@Param("userId") Integer userId);

    @Query("SELECT AVG(ubm.rating) FROM UserBookMark ubm WHERE ubm.user.id = :userId AND ubm.rating IS NOT NULL")
    Double getAverageRating(@Param("userId") Integer userId);

    @Query("SELECT bd.language, COUNT(bd.language) FROM UserBookMark ubm JOIN ubm.bookDescription bd WHERE ubm.user.id = :userId GROUP BY bd.language")
    List<Object[]> countByLanguage(@Param("userId") Integer userId);

}
