package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.BookInstance;

@Repository
public interface BookInstanceRepository extends JpaRepository<BookInstance, Integer> {

}
