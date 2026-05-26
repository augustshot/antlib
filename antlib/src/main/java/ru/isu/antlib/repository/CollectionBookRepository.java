package ru.isu.antlib.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.isu.antlib.model.Collection;
import ru.isu.antlib.model.CollectionBook;
import ru.isu.antlib.model.CollectionBookId;
import ru.isu.antlib.model.UserBookMark;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionBookRepository extends JpaRepository<CollectionBook, CollectionBookId> {

    @Query("SELECT cb.userBookMark FROM CollectionBook cb WHERE cb.collection.id = :id")
    Page<UserBookMark> findByCollectionId(Integer id, Pageable pageable);

    @Query("SELECT cb.userBookMark FROM CollectionBook cb WHERE cb.collection.id = :id")
    List<UserBookMark> findAllByCollectionId(Integer id);

    Optional<CollectionBook> findByCollectionIdAndUserBookMarkId(Integer collectionId, Integer userBookMarkId);
}
