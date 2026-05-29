package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.CollectionBook;
import ru.isu.antlib.model.UserBookMark;
import ru.isu.antlib.repository.CollectionBookRepository;

import java.util.List;

@Service
public class CollectionBookService {
    @Autowired
    private CollectionBookRepository collectionBookRepository;

    public List<UserBookMark> getAllByCollectionId(Integer collectionId){
        return collectionBookRepository.findAllByCollectionId(collectionId);
    }

    @Transactional
    public CollectionBook save(CollectionBook collectionBook){
        return collectionBookRepository.save(collectionBook);
    }

    public CollectionBook getByCollectionIdAndBookId(Integer collectionId, Integer userBookMarkId){
        return collectionBookRepository.findByCollectionIdAndUserBookMarkId(collectionId, userBookMarkId).orElse(null);
    }

    @Transactional
    public void delete(CollectionBook collectionBook){
        collectionBookRepository.delete(collectionBook);
    }
}
