package ru.isu.antlib.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="book_description")
public class BookDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message="Поле не должно быть пустым")
    @NotNull(message="Поле не должно быть пустым")
    private String title;
    @NotBlank(message="Поле не должно быть пустым")
    @NotNull(message="Поле не должно быть пустым")
    private String author;
    private String ISBN;
    @Max(value=2026, message="Введите корректный год издания")
    private Integer year;
    @Min(value=1, message = "Введите корректное число страниц")
    private Integer pages;
    private String language;
    private String publisher;
    private String cover;
    private String description;
    private Boolean verified;


    @Override
    public String toString() {
        return "BookDescription{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", year=" + year +
                ", pages=" + pages +
                ", language='" + language + '\'' +
                ", publisher='" + publisher + '\'' +
                ", cover='" + cover + '\'' +
                ", description='" + description + '\'' +
                ", verified=" + verified +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookDescription that = (BookDescription) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(author, that.author) &&
                Objects.equals(ISBN, that.ISBN) &&
                Objects.equals(year, that.year) &&
                Objects.equals(pages, that.pages) &&
                Objects.equals(language, that.language) &&
                Objects.equals(publisher, that.publisher) &&
                Objects.equals(cover, that.cover) &&
                Objects.equals(description, that.description);
    }

}
