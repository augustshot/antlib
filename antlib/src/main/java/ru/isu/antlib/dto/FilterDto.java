package ru.isu.antlib.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterDto {
    private String title;
    private String author;
    private String language;
    private String isbn;
    private String ratingFrom;
    private String ratingTo;
    private String status;
    private String source;
}
