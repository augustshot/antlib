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

//    ищем неофициальную книжку для проверки дубликатов книг пользователя
    BookDescription findByISBNAndVerifiedFalse(String isbn);

    List<BookDescription> findByTitleContainingIgnoreCaseAndVerifiedTrue(String title);
    List<BookDescription> findByAuthorContainingIgnoreCaseAndVerifiedTrue(String author);
    List<BookDescription> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndVerifiedTrue(String title, String author);

    BookDescription findByISBN(String isbn);
    
    List<BookDescription> findByTitleContainingIgnoreCase(String title);
    List<BookDescription> findByAuthorContainingIgnoreCase(String title);
    List<BookDescription> findByLanguage(String language);
    

    List<BookDescription> findByVerifiedTrue();
    List<BookDescription> findByVerifiedFalse();
    
    void deleteByISBNAndVerifiedFalse(String isbn);
    
}
