package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.BookDescription;
import ru.isu.antlib.repository.BookDescriptionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookDescriptionService {
    @Autowired
    private BookDescriptionRepository bookDescriptionRepository;

    @Transactional
    public BookDescription save(BookDescription bookDescription) {
        return bookDescriptionRepository.save(bookDescription);
    }

    public BookDescription findByISBNVerified(String isbn){
        return bookDescriptionRepository.findByISBNAndVerifiedTrue(isbn);
    }

    public Optional<BookDescription> findEqualByISBN(String isbn, BookDescription book){
        List<BookDescription> books = bookDescriptionRepository.findAllByISBNAndVerifiedFalse(isbn);
        return books.stream()
                .filter(s -> s.equals(book))
                .findFirst();
    }

    @Transactional
    public void deleteById(Integer id){
        bookDescriptionRepository.deleteById(id);
    }

    public Optional<BookDescription> getById(Integer id){
        return bookDescriptionRepository.findById(id);
    }

}
