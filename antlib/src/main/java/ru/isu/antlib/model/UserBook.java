package ru.isu.antlib.model;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBook {
    @Valid
    private UserBookMark userBookMark;
    @Valid
    private BookDescription bookDescription;
}
