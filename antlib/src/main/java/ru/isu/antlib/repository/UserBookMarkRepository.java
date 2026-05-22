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
import ru.isu.antlib.model.UserBookMark;

import java.util.List;

@Repository
public interface UserBookMarkRepository extends JpaRepository<UserBookMark, Integer>, JpaSpecificationExecutor<UserBookMark> {

    @Query("select ubm from UserBookMark ubm join fetch ubm.bookDescription where ubm.id = :id")
    UserBookMark findBookByUserBookMarkId(Integer id);

    @Query("select ubm from UserBookMark ubm join fetch ubm.bookDescription where ubm.user.id = :userId")
    Page<UserBookMark> findAllBooks(@Param("userId") Integer userId, Pageable pageable);

    @Query("select ubm from UserBookMark ubm join fetch ubm.bookDescription where ubm.user.id = :userId and ubm.bookDescription.ISBN = :ISBN")
    UserBookMark findBookByUserIdAndISBN(@Param("userId") Integer userId, @Param("ISBN") String isbn);


    // фильтры - specification?
    // title
    // author
    List<UserBookMark> findByUserIdAndSource(Integer userId, Source source);
    List<UserBookMark> findByUserIdAndStatus(Integer userId, Status status);
    List<UserBookMark> findByUserIdAndRatingBetween(Integer userId, Integer minRating, Integer maxRating);
    List<UserBookMark> findByUserIdAndRating(Integer userId, Integer rating);
    void deleteByUserId(Integer userId);



}
