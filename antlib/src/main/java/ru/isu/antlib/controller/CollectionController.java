package ru.isu.antlib.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/collections")
public class CollectionController {
    @GetMapping("/")
    public String collections(){
        return "collections/allCollections";
    }
}
