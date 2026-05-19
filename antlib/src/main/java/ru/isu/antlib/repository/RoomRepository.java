package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

}
