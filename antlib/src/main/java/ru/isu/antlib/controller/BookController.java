package ru.isu.antlib.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BookController {
    @GetMapping("/")
    public String books(){
        return "books/allBooks";
    }
}
