package ru.isu.antlib.controller;

import jakarta.persistence.AttributeConverter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.*;
import java.util.stream.Collectors;

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
        

        // есть в бд?
        BookDescription book = bookDescriptionService.findByISNBVerified(isbn);
        if(book != null){
            UserBook userBook = new UserBook();
            userBook.setBookDescription(book);
            model.addAttribute("userBook", userBook);
            return "books/addBook";
        }

        // Поиск книги
        book = BookParser.findByISBN(isbn); // офиц книга

        if (book != null) {
            book.setVerified(true);
            bookDescriptionService.save(book);
            UserBook userBook = new UserBook();
            userBook.setBookDescription(book);
            model.addAttribute("userBook", userBook);
        } else {
            model.addAttribute("errorMessage", "Книга не найдена. Проверьте корректность ISBN или введите данные вручную");
            model.addAttribute("userBook", new UserBook());
        }
        return "books/addBook";
    }


    @PostMapping("/saveMultiple")
    public String saveMultiple(@RequestParam(value="multipleIsbn") String isbnListStr, Model model, RedirectAttributes redirectAttributes){
        String[] isbnArray = isbnListStr.split("\\r?\\n");
        Set<String> isbnList = new HashSet<String>();
        for (String s : isbnArray) {
            String cleaned = s.trim().replaceAll("[\\s-]", "");
            if (!cleaned.isEmpty()) {
                cleaned = (cleaned.length() == 10 ? "978" + cleaned : cleaned);
                isbnList.add(cleaned);
            }
        }
//        !!!!!!!!!!!!!!!!!!
        Optional<User> currentUser = userRepository.findById(1);

        ArrayList<String> added = new ArrayList<>();
        ArrayList<String> skipped = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();

        // много книжек сохраняются сразу без вызова /save
        for(String s : isbnList){
            // уже есть у пользователя?
            UserBookMark userBookMark = userBookMarkService.getByUserIdAndISBN(currentUser.get().getId(), s);
            if(userBookMark != null){
                duplicates.add(s);
            }
            else{
                // есть в бд?
                BookDescription book = bookDescriptionService.findByISNBVerified(s);
                userBookMark = new UserBookMark();
                if(book != null){
                    userBookMark.setBookDescription(book);
                    userBookMark.setUser(currentUser.get());
                    userBookMarkService.save(userBookMark);
                    added.add(s);
                }else{
                    // нет в бд, нет у пользователя - добавляем
                    book = BookParser.findByISBN(s);
                    if (book != null) {
                        book.setVerified(true);
                        bookDescriptionService.save(book);
                        userBookMark.setBookDescription(book);
                        userBookMark.setUser(currentUser.get());
                        userBookMarkService.save(userBookMark);
                        added.add(s);
                    }
                    else{
                        skipped.add(s);
                    }
                }
            }
        }

        redirectAttributes.addFlashAttribute("added", added);
        redirectAttributes.addFlashAttribute("skipped", skipped);
        redirectAttributes.addFlashAttribute("duplicates", duplicates);
        redirectAttributes.addFlashAttribute("showModal", true);
        String summaryMessage = String.format("Добавлено: %d, пропущено: %d, дубликаты: %d",
                added.size(), skipped.size(), duplicates.size());

        redirectAttributes.addFlashAttribute("summaryMessage", summaryMessage);

        return "redirect:/books/";
    }

    @PostMapping("/save")
    public String saveSingle(
            @Valid @ModelAttribute("userBook") UserBook userBook,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors())
            return "books/addBook";

//        !!!!!!!!!!!!!!!
        Optional<User> currentUser = userRepository.findById(1);
        BookDescription book = userBook.getBookDescription();

        // проверяем, есть ли у юзера книга с таким исбн
        UserBookMark userBookMark = userBookMarkService.getByUserIdAndISBN(currentUser.get().getId(), book.getISBN());
        if(userBookMark != null){
            model.addAttribute("errorMessage", "В вашей библиотеке уже есть книга с таким ISBN");
            return "books/addBook";
        }

        UserBookMark mark = userBook.getUserBookMark();
        mark.setUser(currentUser.get());

        // проверяем, есть ли офиц книга с таким исбн в бд и если да то equals
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

        return "redirect:/books/book/" + mark.getId();
    }

    @GetMapping("/book/{id}")
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
