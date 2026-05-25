package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.Library;
import ru.isu.antlib.model.User;
import ru.isu.antlib.model.UserLibrary;
import ru.isu.antlib.repository.UserLibraryRepository;

import java.util.List;
import java.util.Map;

@Service
public class UserLibraryService {
    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @Transactional
    public UserLibrary save(UserLibrary userLibrary){
        return userLibraryRepository.save(userLibrary);
    }

    public List<UserLibrary> findByLibraryId(Integer libraryId) {
        return userLibraryRepository.findMembersByLibraryId(libraryId);
    }

    public boolean isMember(User user, Library library){
        return userLibraryRepository.existsByUserIdAndLibraryId(user.getId(), library.getId());
    }

    public UserLibrary getByUserAndLibrary(Integer userId, Integer libraryId){
        return userLibraryRepository.findByUserIdAndLibraryId(userId, libraryId);
    }

    public void delete(UserLibrary userLibrary){
        userLibraryRepository.delete(userLibrary);
    }

    public User getOwner(Library library){
        return userLibraryRepository.findOwnerByLibraryId(library.getId());
    }

    public Integer memberCount(Integer libraryId){
        return userLibraryRepository.countByLibraryId(libraryId);
    }

    public List<UserLibrary> getAllByUser(User user){
        return userLibraryRepository.findAllByUserId(user.getId());
    }



}
