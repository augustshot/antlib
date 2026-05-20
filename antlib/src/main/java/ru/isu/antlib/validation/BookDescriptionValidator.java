package ru.isu.antlib.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.isu.antlib.model.UserBook;
import ru.isu.antlib.model.BookDescription;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class BookDescriptionValidator implements Validator {
    @Override
    public boolean supports(Class<?> type) {
        return UserBook.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors e) {
//        ValidationUtils.rejectIfEmpty(e,"number", "error.number.empty", "Введите номер рейса" );
        UserBook userBook = (UserBook) o;
        BookDescription userBookMark = userBook.getBookDescription();
        String isbn = userBookMark.getISBN();
        String isbn13 = "^(978|979)-?\\d-?\\d{4}-?\\d{4}-?\\d$";
        String isbn10 = "^(\\d-?){9}\\d$";
        if(isbn != null && !isbn.isBlank() && !Pattern.matches(isbn10, isbn) && !Pattern.matches(isbn13, isbn)){
            e.rejectValue("bookDescription.ISBN", "ISBN.InvalidFormat", "Некорректный формат ISBN");
        }

    }
}
