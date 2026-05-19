package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {

}
