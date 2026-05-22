package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.BookDescription;
import ru.isu.antlib.model.UserBookMark;

import java.util.List;

@Repository
public interface BookDescriptionRepository extends JpaRepository<BookDescription, Integer>, JpaSpecificationExecutor<UserBookMark> {

//    ищем официальную книжку
    BookDescription findByISBNAndVerifiedTrue(String isbn);


    List<BookDescription> findAllByISBNAndVerifiedFalse(String isbn);
    
    void deleteByISBNAndVerifiedFalse(String isbn);
    
}
