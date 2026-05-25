package ru.isu.antlib.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.isu.antlib.dto.UserBookDto;
import ru.isu.antlib.model.BookDescription;

import java.util.regex.Pattern;

public class BookDescriptionValidator implements Validator {
    @Override
    public boolean supports(Class<?> type) {
        return UserBookDto.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors e) {
//        ValidationUtils.rejectIfEmpty(e,"number", "error.number.empty", "Введите номер рейса" );
        UserBookDto userBook = (UserBookDto) o;
        BookDescription userBookMark = userBook.getBookDescription();
        String isbn = userBookMark.getISBN().replace("-", "");
        String isbn13 = "^(97)\\d{11}$";
        String isbn10 = "^\\d{9}[\\dX]$";
        if(isbn != null && !isbn.isBlank() && !Pattern.matches(isbn10, isbn) && !Pattern.matches(isbn13, isbn)){
            e.rejectValue("bookDescription.ISBN", "ISBN.InvalidFormat", "Некорректный формат ISBN");
        }

    }
}
