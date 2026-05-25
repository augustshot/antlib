package ru.isu.antlib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.isu.antlib.model.Room;
import ru.isu.antlib.repository.RoomRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getRoomsByLibraryId(Integer libraryId) {
        return roomRepository.findByLibraryId(libraryId);
    }

    public Room save(Room room){
        return roomRepository.save(room);
    }

    public boolean exists(Room room){
        return roomRepository.existsByNameAndLibraryId(room.getName(), room.getLibrary().getId());
    }
    
    public Optional<Room> getById(Integer roomId) {
        return roomRepository.findById(roomId);
    }

    public void deleteById(Integer id){
        roomRepository.deleteById(id);
    }

}
