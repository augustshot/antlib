package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.Collection;
import ru.isu.antlib.model.User;
import ru.isu.antlib.model.UserBookMark;
import ru.isu.antlib.repository.CollectionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private UserBookMarkService userBookMarkService;

    @Transactional
    public Collection save(Collection collection){
        collection.setLastUpdate(LocalDateTime.now());
        return collectionRepository.save(collection);
    }

    @Transactional
    public void deleteById(Integer collectionId){
        collectionRepository.deleteById(collectionId);
    }

    public boolean existsByNameAndUser(String name, User user){
        return collectionRepository.findByNameAndUserId(name, user.getId()).isPresent();
    }
    
    public Optional<Collection> getById(Integer id){
        return collectionRepository.findById(id);
    }


}
