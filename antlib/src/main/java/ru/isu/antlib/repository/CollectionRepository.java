package ru.isu.antlib.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Collection;
import ru.isu.antlib.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    List<Collection> findAllByUserId(Integer userId);
    Page<Collection> findByUser(User user, Pageable pageable);

    Optional<Collection> findByNameAndUserId(String name, Integer userId);


}
