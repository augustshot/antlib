package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.antlib.model.Library;
import ru.isu.antlib.model.Shelf;
import ru.isu.antlib.model.ShelfBook;
import ru.isu.antlib.repository.ShelfBookRepository;
import ru.isu.antlib.repository.ShelfRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ShelfBookService {
    @Autowired
    private ShelfBookRepository shelfBookRepository;


    public List<ShelfBook> getByShelfId(Integer shelfId) {
        return shelfBookRepository.findByShelfId(shelfId);
    }

    @Transactional
    public ShelfBook save(ShelfBook shelfBook){
        return shelfBookRepository.save(shelfBook);
    }


    public Integer countBooks(Shelf shelf){
        return shelfBookRepository.countByShelfId(shelf.getId());
    }

    public List<Map<String, Object>> searchLibrary(Library library, String search){
           return shelfBookRepository.searchBooksInLibrary(library.getId(), search);
    }

    @Transactional
    public void deleteById(Integer id){
        shelfBookRepository.deleteById(id);
    }

}
