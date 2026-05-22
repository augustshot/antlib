package ru.isu.antlib.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
}
