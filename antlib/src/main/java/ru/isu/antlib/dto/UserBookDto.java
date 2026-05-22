package ru.isu.antlib.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import ru.isu.antlib.model.BookDescription;
import ru.isu.antlib.model.UserBookMark;

@Getter
@Setter
public class UserBookDto {
    @Valid
    private UserBookMark userBookMark = new UserBookMark();
    @Valid
    private BookDescription bookDescription = new BookDescription();
}
