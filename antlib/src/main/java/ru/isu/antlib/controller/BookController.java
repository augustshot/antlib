package ru.isu.antlib.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.isu.antlib.model.*;
import ru.isu.antlib.service.BookDescriptionService;
import ru.isu.antlib.service.UserService;
import ru.isu.antlib.validation.UserBookMarkDateValidator;
import ru.isu.antlib.repository.BookDescriptionRepository;
import ru.isu.antlib.repository.UserBookMarkRepository;
import ru.isu.antlib.repository.UserRepository;
import ru.isu.antlib.service.UserBookMarkService;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    @Autowired
    private UserBookMarkService userBookMarkService;
    @Autowired
    private BookDescriptionService bookDescriptionService;
    @Autowired
    private UserService userService;

    @Autowired
    private BookDescriptionRepository bookDescriptionRepository;
    @Autowired
    private UserBookMarkRepository userBookMarkRepository;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("statuses")
    public List<Status> getStatuses(){
        return Arrays.asList(
                Status.READING,
                Status.FINISHED,
                Status.PLANNED,
                Status.POSTPONED
        );
    }

    @ModelAttribute("sources")
    public List<Source> getSources(){
        return Arrays.asList(
                Source.BORROWED,
                Source.EBOOK,
                Source.OWNED,
                Source.SHARED
        );
    }


    @GetMapping("/")
    public String books(Model model, Pageable page, Sort sort){
        Integer userId = 1;

        Pageable pageNew = PageRequest.of(page.getPageNumber(), 30, page.getSort());

        Sort.Order order = null;
        if(sort!=null && sort.iterator().hasNext()){
            order = sort.iterator().next();
        }

        model.addAttribute("sort", (order!=null)?order.getProperty():"");
        model.addAttribute("dir", (order!=null)?order.getDirection():"");

        model.addAttribute("page", userBookMarkRepository.findAllBooks(userId, pageNew));
        return "books/allBooks";
    }

    @GetMapping("add")
    public String addBook(Model model){
        if (!model.containsAttribute("userBook")) {
            model.addAttribute("userBook", new UserBook());
        }
        return "books/addBook";
    }


    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("userBook") UserBook userBook,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes attributes) {

        if (bindingResult.hasErrors())
            return "books/addBook";

        Optional<User> currentUser = userRepository.findById(1);

        BookDescription book = userBook.getBookDescription();
//        !!!!!!!!!!!!!!!!!!!
        book.setVerified(false);
        bookDescriptionService.save(book);

        UserBookMark mark = userBook.getUserBookMark();
        mark.setBookDescription(book);
        mark.setUser(currentUser.get());
        userBookMarkService.save(mark);

        return "redirect:/books/" + mark.getId();
    }

    @GetMapping("/{id}")
    public String bookInfo(Model model, @PathVariable Integer id){
        UserBookMark userBookMark = userBookMarkService.getByUserBookMarkId(id);
        model.addAttribute("userBookMark", userBookMark);
        return "books/bookInfo";
    }

    @InitBinder("userBook")
    public void initBinder(WebDataBinder binder){
        binder.addValidators(new UserBookMarkDateValidator());
    }
}
