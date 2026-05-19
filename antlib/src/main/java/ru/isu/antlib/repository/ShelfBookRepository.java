package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.ShelfBook;

@Repository
public interface ShelfBookRepository extends JpaRepository<ShelfBook, Integer> {

}
