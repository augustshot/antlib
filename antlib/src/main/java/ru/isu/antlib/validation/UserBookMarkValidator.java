package ru.isu.antlib.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.isu.antlib.dto.UserBookDto;
import ru.isu.antlib.model.UserBookMark;

import java.time.LocalDate;

public class UserBookMarkValidator implements Validator {
    @Override
    public boolean supports(Class<?> type) {
        return UserBookDto.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors e) {
//        ValidationUtils.rejectIfEmpty(e,"number", "error.number.empty", "Введите номер рейса");
        UserBookDto userBook = (UserBookDto) o;
        UserBookMark userBookMark = userBook.getUserBookMark();
        LocalDate start = userBookMark.getDateStart();
        LocalDate finish = userBookMark.getDateFinish();
        if(start==null && finish!=null){
            e.rejectValue("userBookMark.dateFinish", "error.date.error", "Введите обе даты");
        }
        if (start!=null && finish!=null && start.isAfter(finish)) {
            e.rejectValue("userBookMark.dateFinish", "error.date.error", "Дата окончания не может быть раньше даты начала");
        }
    }
}
