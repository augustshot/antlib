package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.isu.antlib.model.BookDescription;
import ru.isu.antlib.repository.BookDescriptionRepository;

@Service
public class BookDescriptionService {
    @Autowired
    private BookDescriptionRepository bookDescriptionRepository;

    public BookDescription save(BookDescription bookDescription) {
        return bookDescriptionRepository.save(bookDescription);
    }

    public BookDescription findByISNBVerified(String isbn){
        return bookDescriptionRepository.findByISBNAndVerifiedTrue(isbn);
    }
}
