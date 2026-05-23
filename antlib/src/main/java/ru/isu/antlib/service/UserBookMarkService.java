package ru.isu.antlib.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.dto.UserBookDto;
import ru.isu.antlib.model.BookDescription;
import ru.isu.antlib.model.Status;
import ru.isu.antlib.model.User;
import ru.isu.antlib.model.UserBookMark;
import ru.isu.antlib.repository.BookDescriptionRepository;
import ru.isu.antlib.repository.UserBookMarkRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBookMarkService {
    @Autowired
    private UserBookMarkRepository userBookMarkRepository;

    @Autowired
    private BookDescriptionService bookDescriptionService;

    @Transactional
    public UserBookMark save(UserBookMark userBookMark) {
        return userBookMarkRepository.save(userBookMark);
    }

    public UserBookMark getByUserBookMarkId(Integer id){
        return userBookMarkRepository.findBookByUserBookMarkId(id);
    }

    public UserBookMark getByUserIdAndISBN(Integer userId, String isbn){
        return userBookMarkRepository.findBookByUserIdAndISBN(userId, isbn);
    }

    @Transactional
    public void deleteBook(UserBookMark userBookMark){
        BookDescription book = userBookMark.getBookDescription();
        userBookMarkRepository.deleteById(userBookMark.getId());
        if(!book.getVerified()) bookDescriptionService.deleteById(book.getId());
    }

    public long[] getStats(Integer userId){
        long[] stats = new long[4];
        List<UserBookMark> books = userBookMarkRepository.findAllBooks(userId);
        stats[0] = books.stream().filter(b -> b.getStatus().equals(Status.PLANNED)).count();
        stats[1] = books.stream().filter(b -> b.getStatus().equals(Status.READING)).count();
        stats[2] = books.stream().filter(b -> b.getStatus().equals(Status.FINISHED)).count();
        stats[3] = books.stream().filter(b -> b.getStatus().equals(Status.POSTPONED)).count();
        return stats;
    }
}
