package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Library;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Integer> {

}
