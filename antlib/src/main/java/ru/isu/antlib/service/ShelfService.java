package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.isu.antlib.model.Shelf;
import ru.isu.antlib.repository.ShelfRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ShelfService {
    @Autowired
    private ShelfRepository shelfRepository;

    public List<Shelf> getShelvesByRoomId(Integer roomId) {
        return shelfRepository.findByRoomId(roomId);
    }

    public Optional<Shelf> getById(Integer id){
        return shelfRepository.findById(id);
    }

    public Shelf save(Shelf shelf){
        return shelfRepository.save(shelf);
    }

    public void deleteById(Integer shelfId){
        shelfRepository.deleteById(shelfId);
    }
}
