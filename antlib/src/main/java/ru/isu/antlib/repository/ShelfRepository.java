package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Shelf;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {

}
