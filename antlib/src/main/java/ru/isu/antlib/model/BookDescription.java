package ru.isu.antlib.model;


import jakarta.persistence.*;
import lombok.*;

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

    private String title;
    private String author;
    private String ISBN;
    private Integer year;
    private Integer pages;
    private String language;
    private String publisher;
    private String cover;
    private String description;

}
