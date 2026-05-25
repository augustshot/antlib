package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByLibraryId(Integer libraryId);

    boolean existsByNameAndLibraryId(String name, Integer libraryId);

}
