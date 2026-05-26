package ru.isu.antlib.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.dto.UserBookDto;
import ru.isu.antlib.model.*;
import ru.isu.antlib.repository.BookDescriptionRepository;
import ru.isu.antlib.repository.UserBookMarkRepository;

import java.util.Arrays;
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

    public List<UserBookMark> getAllByUser(User user){
        return userBookMarkRepository.findAllByUserId(user.getId());
    }

    public UserBookMark getById(Integer id){
        return userBookMarkRepository.findBookByUserBookMarkId(id);
    }

    public UserBookMark getByUserIdAndISBN(Integer userId, String isbn){
        return userBookMarkRepository.findBookByUserIdAndISBN(userId, isbn);
    }

    public UserBookMark getByUserAndBookDescription(User user, BookDescription bookDescription){
        return userBookMarkRepository.findByUserIdAndBookDescriptionId(user.getId(), bookDescription.getId()).orElse(null);
    }

    @Transactional
    public void deleteBook(UserBookMark userBookMark){
        BookDescription book = userBookMark.getBookDescription();
        userBookMarkRepository.deleteById(userBookMark.getId());
        if (!book.getVerified()) {
                bookDescriptionService.deleteById(book.getId());
            }
    }

    public List<UserBookMark> getByUserAndSourceAndSearch(User user, Source source, String search){
        return userBookMarkRepository.findByUserIdAndSourceAndSearch(user.getId(), source, search);
    }

    public List<UserBookMark> getByUserAndSource(User user, Source source){
        return userBookMarkRepository.findByUserIdAndSource(user.getId(), source);
    }

    public List<UserBookMark> getDistinctByUserAndSearch(User user, String search) {
        return userBookMarkRepository.findDistinctByUserIdAndSearch(user.getId(), search);
    }

    // статистика

    public int[] getStatusStats(Integer userId){
        int[] stats = new int[5];
        stats[0] = userBookMarkRepository.countByUserIdAndStatus(userId, Status.PLANNED);
        stats[1] = userBookMarkRepository.countByUserIdAndStatus(userId, Status.READING);
        stats[2] = userBookMarkRepository.countByUserIdAndStatus(userId, Status.FINISHED);
        stats[3] = userBookMarkRepository.countByUserIdAndStatus(userId, Status.POSTPONED);
        stats[4] = userBookMarkRepository.countByUserIdAndNoStatus(userId);

        return stats;
    }

    public int[] getSourceStats(Integer userId){
        int[] stats = new int[5];
        stats[0] = userBookMarkRepository.countByUserIdAndSource(userId, Source.OWNED);
        stats[1] = userBookMarkRepository.countByUserIdAndSource(userId, Source.EBOOK);
        stats[2] = userBookMarkRepository.countByUserIdAndSource(userId, Source.BORROWED);
        stats[3] = userBookMarkRepository.countByUserIdAndSource(userId, Source.SHARED);
        stats[4] = userBookMarkRepository.countByUserIdAndNoSource(userId);
        return stats;
    }

    public Double getAverageRating(Integer userId){
        return userBookMarkRepository.getAverageRating(userId);
    }

    public Integer getTotalPages(Integer userId){
        return userBookMarkRepository.getTotalPagesByUserIdAndFinished(userId);
    }

    public List<Object[]> getLanguageStats(Integer userId){
        return userBookMarkRepository.countByLanguage(userId);
    }





}
