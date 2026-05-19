package ru.isu.antlib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.UserLibrary;
import ru.isu.antlib.model.UserLibraryId;

@Repository
public interface UserLibraryRepository extends JpaRepository<UserLibrary, UserLibraryId> {

}
