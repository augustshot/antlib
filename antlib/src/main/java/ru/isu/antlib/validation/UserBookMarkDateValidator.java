package ru.isu.antlib.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.isu.antlib.model.UserBook;
import ru.isu.antlib.model.UserBookMark;

import java.time.LocalDate;

public class UserBookMarkDateValidator implements Validator {
    @Override
    public boolean supports(Class<?> type) {
        return UserBook.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors e) {
//        ValidationUtils.rejectIfEmpty(e,"number", "error.number.empty", "Введите номер рейса");
        UserBook userBook = (UserBook) o;
        UserBookMark userBookMark = userBook.getUserBookMark();
        LocalDate start = userBookMark.getDateStart();
        LocalDate finish = userBookMark.getDateFinish();
        if(start==null && finish!=null){
            e.rejectValue("userBookMark.dateFinish", "error.date.error", "Введите обе даты");
        }
        if(start!=null && finish==null){
            e.rejectValue("userBookMark.dateStart", "error.date.error", "Введите обе даты");
        }
        if (start!=null && finish!=null && start.isAfter(finish)) {
            e.rejectValue("userBookMark.dateFinish", "error.date.error", "Дата окончания не может быть раньше даты начала");
        }
    }
}
