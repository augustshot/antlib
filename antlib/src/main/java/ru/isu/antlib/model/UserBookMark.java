package ru.isu.antlib.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="user_book_mark")
public class UserBookMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="book_description_id")
    private BookDescription bookDescription;

    @Range(min=1, max=5, message="Введите оценку от 1 до 5")
    private Integer rating;
    @Enumerated(EnumType.STRING)
    private Source source;
    @Enumerated(EnumType.STRING)
    private Status status;
    @PastOrPresent(message="Дата не может быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="date_start")
    private LocalDate dateStart;

    @PastOrPresent(message="Дата не может быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="date_finish")
    private LocalDate dateFinish;
    private String review;

    @Override
    public String toString() {
        return "UserBookMark{" +
                "id=" + id +
                ", user=" + user +
                ", bookDescription=" + bookDescription +
                ", rating=" + rating +
                ", source=" + source +
                ", status=" + status +
                ", dateStart=" + dateStart +
                ", dateFinish=" + dateFinish +
                ", review=" + review +
                '}';
    }
}


