package ru.isu.antlib.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.isu.antlib.model.UserBookMark;

public class UserBookMarkSpecification {

    public static Specification<UserBookMark> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<UserBookMark> hasSource(String source) {
        return (root, query, criteriaBuilder) -> {
            if (source == null || source.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("source"), source);
        };
    }

    public static Specification<UserBookMark> ratingBetween(Integer ratingFrom, Integer ratingTo) {
        return (root, query, criteriaBuilder) -> {
            if (ratingFrom == null && ratingTo == null) {
                return criteriaBuilder.conjunction();
            }
            if (ratingFrom != null && ratingTo != null) {
                return criteriaBuilder.between(root.get("rating"), ratingFrom, ratingTo);
            } else if (ratingFrom != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), ratingFrom);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), ratingTo);
            }
        };
    }

    public static Specification<UserBookMark> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bookDescription").get("title")),
                    "%" + title.toLowerCase() + "%"
            );
        };
    }

    public static Specification<UserBookMark> authorContains(String author) {
        return (root, query, criteriaBuilder) -> {
            if (author == null || author.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bookDescription").get("author")),
                    "%" + author.toLowerCase() + "%"
            );
        };
    }

    public static Specification<UserBookMark> languageContains(String language) {
        return (root, query, criteriaBuilder) -> {
            if (language == null || language.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("bookDescription").get("language")),
                    "%" + language.toLowerCase() + "%"
            );
        };
    }

    public static Specification<UserBookMark> isbnEquals(String isbn) {
        return (root, query, criteriaBuilder) -> {
            if (isbn == null || isbn.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("bookDescription").get("ISBN"), isbn);
        };
    }

    public static Specification<UserBookMark> byUserId(Integer userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }
}