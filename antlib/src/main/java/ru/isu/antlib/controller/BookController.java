package ru.isu.antlib.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.UndertowServletWebServerFactoryCustomizer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.isu.antlib.model.*;
import ru.isu.antlib.repository.UserLibraryRepository;
import ru.isu.antlib.service.BookDescriptionService;
import ru.isu.antlib.service.UserService;
import ru.isu.antlib.validation.BookDescriptionValidator;
import ru.isu.antlib.validation.UserBookMarkValidator;
import ru.isu.antlib.repository.BookDescriptionRepository;
import ru.isu.antlib.repository.UserBookMarkRepository;
import ru.isu.antlib.repository.UserRepository;
import ru.isu.antlib.service.UserBookMarkService;
import org.springframework.ui.Model;
import ru.isu.antlib.parser.BookParser;

import java.util.ArrayList;
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

    @Autowired
    private UserLibraryRepository userLibraryRepository;


    @ModelAttribute("statuses")
    public List<Status> getStatuses(){
        return Arrays.asList(
                Status.PLANNED,
                Status.READING,
                Status.FINISHED,
                Status.POSTPONED
        );
    }

    @ModelAttribute("sources")
    public List<Source> getSources(){
        return Arrays.asList(
                Source.OWNED,
                Source.EBOOK,
                Source.BORROWED,
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
        model.addAttribute("isbn", "");
        return "books/addBook";
    }

    @GetMapping("searchISBN")
    public String searchByIsbn(@RequestParam(value="isbn") String isbn, Model model) {
        List<String> isbnList = parseIsbnList(isbn);

        if (isbnList.size() > 1) {
            return "books/addBook";
        }
        
        isbn = (isbn.length() == 10 ? "978" + isbn : isbn);

        // есть в бд?
        BookDescription book = bookDescriptionService.findByISNBVerified(isbn);
        if(book != null){
            UserBook userBook = new UserBook();
            userBook.setBookDescription(book);
            model.addAttribute("userBook", userBook);
            model.addAttribute("isbn", "");
            return "books/addBook";
        }

        // Поиск книги
        book = BookParser.findByISBN(isbnList.get(0)); // офиц книга

        if (book != null) {
            book.setVerified(true);
            bookDescriptionService.save(book);
            UserBook userBook = new UserBook();
            userBook.setBookDescription(book);
            model.addAttribute("userBook", userBook);
        } else {
            model.addAttribute("userBook", new UserBook());
        }
        model.addAttribute("isbn", "");
        return "books/addBook";
    }

    private List<String> parseIsbnList(String isbnString) {
        if (isbnString == null || isbnString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();

        // Разделяем по: пробелы, запятые, точки с запятой, переносы строк
        String[] parts = isbnString.split("[\\s,;\\n\\r]+");

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;

            // Очищаем от лишних символов (оставляем только цифры и дефисы)
            String cleaned = trimmed.replaceAll("[^\\d-]", "");
            if (cleaned.isEmpty()) continue;
            result.add(cleaned);
        }

        return result;
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

        // проверяем, есть ли у юзера книга с таким исбн
        UserBookMark userBookMark = userBookMarkService.getByUserIdAndISBN(currentUser.get().getId(), book.getISBN());
        if(userBookMark != null){
            model.addAttribute("message", "В вашей библиотеке уже есть книга с таким ISBN");
            return "books/addBook";
        }

        UserBookMark mark = userBook.getUserBookMark();
        mark.setUser(currentUser.get());

        // проверяем, есть ли книга с таким исбн в бд и если да то equals
        BookDescription verified =bookDescriptionService.findByISNBVerified(book.getISBN());
        if(verified != null && verified.equals(book)){
            mark.setBookDescription(verified);
        }
        else{
            book.setVerified(false);
            bookDescriptionService.save(book);
            mark.setBookDescription(book);
        }


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
        binder.addValidators(new UserBookMarkValidator());
        binder.addValidators(new BookDescriptionValidator());
    }

}
