package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.BookDescription;
import ru.isu.antlib.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
