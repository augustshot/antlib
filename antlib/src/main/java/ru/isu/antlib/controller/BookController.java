package ru.isu.antlib.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.isu.antlib.dto.FilterDto;
import ru.isu.antlib.dto.UserBookDto;
import ru.isu.antlib.exception.BookNotFoundException;
import ru.isu.antlib.exception.BookTimeoutException;
import ru.isu.antlib.model.*;
import ru.isu.antlib.repository.UserLibraryRepository;
import ru.isu.antlib.service.*;
import ru.isu.antlib.specification.UserBookMarkSpecification;
import ru.isu.antlib.validation.BookDescriptionValidator;
import ru.isu.antlib.validation.UserBookMarkValidator;
import ru.isu.antlib.repository.BookDescriptionRepository;
import ru.isu.antlib.repository.UserBookMarkRepository;
import ru.isu.antlib.repository.UserRepository;
import org.springframework.ui.Model;

import java.util.*;

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


    @GetMapping
    public String books(Model model,
                        @RequestParam(required = false) String sort,
                        @RequestParam(required = false) String dir,
                        Pageable page, @AuthenticationPrincipal UserDetails auth,
                        @ModelAttribute("filter") FilterDto filter) {

        Integer userId = userRepository.findByUsername(auth.getUsername()).get().getId();

        String sortField = "title";
        Sort.Direction direction = Sort.Direction.ASC;

        if (sort != null && !sort.isEmpty()) {
            sortField = sort;
            direction = (dir != null && dir.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        }

        String fullSortField = "bookDescription." + sortField;
        Sort sortObj = Sort.by(direction, fullSortField);
        Pageable pageable = PageRequest.of(page.getPageNumber(), 20, sortObj);


        Specification<UserBookMark> spec = Specification.unrestricted();
        spec = spec
                .and(UserBookMarkSpecification.byUserId(userId))
                .and(UserBookMarkSpecification.titleContains(filter.getTitle()))
                .and(UserBookMarkSpecification.authorContains(filter.getAuthor()))
                .and(UserBookMarkSpecification.languageContains(filter.getLanguage()))
                .and(UserBookMarkSpecification.isbnEquals(filter.getIsbn()))
                .and(UserBookMarkSpecification.hasStatus(filter.getStatus()))
                .and(UserBookMarkSpecification.hasSource(filter.getSource()));

        Integer ratingFrom = null;
        Integer ratingTo = null;

        if (filter.getRatingFrom() != null && !filter.getRatingFrom().isEmpty()) {
            try {
                ratingFrom = Integer.parseInt(filter.getRatingFrom());
            } catch (NumberFormatException ignored) {}
        }

        if (filter.getRatingTo() != null && !filter.getRatingTo().isEmpty()) {
            try {
                ratingTo = Integer.parseInt(filter.getRatingTo());
            } catch (NumberFormatException ignored) {}
        }

        spec = spec.and(UserBookMarkSpecification.ratingBetween(ratingFrom, ratingTo));

        Page<UserBookMark> pageResult = userBookMarkRepository.findAll(spec, pageable);

        model.addAttribute("sort", sortField);
        model.addAttribute("dir", direction.toString().toLowerCase());
        model.addAttribute("page", pageResult);
        model.addAttribute("filter", filter);

        return "books/allBooks";
    }

    @GetMapping("add")
    public String addBook(Model model){
        if (!model.containsAttribute("userBook")) {
            model.addAttribute("userBook", new UserBookDto());
        }
        model.addAttribute("isbn", "");
        return "books/addBook";
    }

    @GetMapping("searchISBN")
    public String searchByIsbn(@RequestParam(value="isbn") String isbn, Model model) {
        

        // есть в бд?
        BookDescription book = bookDescriptionService.findByISNBVerified(isbn);
        if(book != null){
            UserBookDto userBook = new UserBookDto();
            userBook.setBookDescription(book);
            model.addAttribute("userBook", userBook);
            return "books/addBook";
        }

        // Поиск книги
        try {
            book = BookParser.findByISBN(isbn); // официальная книга

            if (book != null) {
                book.setVerified(true);
                bookDescriptionService.save(book);
                UserBookDto userBook = new UserBookDto();
                userBook.setBookDescription(book);
                model.addAttribute("userBook", userBook);
            }

        } catch (BookNotFoundException e) {
            // Книга не найдена по ISBN
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userBook", new UserBookDto());

        } catch (BookTimeoutException e) {
            // Таймаут при парсинге
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userBook", new UserBookDto());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Произошла неизвестная ошибка: " + e.getMessage());
            model.addAttribute("userBook", new UserBookDto());
            e.printStackTrace(); // Для отладки
        }

        return "books/addBook";
    }


    @PostMapping("/saveMultiple")
    public String saveMultiple(@RequestParam(value="multipleIsbn") String isbnListStr,
                               Model model, RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal UserDetails auth){
        String[] isbnArray = isbnListStr.split("\\r?\\n");
        Set<String> isbnList = new HashSet<String>();
        for (String s : isbnArray) {
            String cleaned = s.trim().replaceAll("[\\s-]", "");
            if (!cleaned.isEmpty()) {
                isbnList.add(cleaned);
            }
        }

        User currentUser = userRepository.findByUsername(auth.getUsername()).get();

        ArrayList<String> added = new ArrayList<>();
        ArrayList<String> skipped = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();

        // много книжек сохраняются сразу без вызова /save
        for(String s : isbnList){
            // уже есть у пользователя?
            UserBookMark userBookMark = userBookMarkService.getByUserIdAndISBN(currentUser.getId(), s);
            if(userBookMark != null){
                duplicates.add(s);
            }
            else{
                // есть в бд?
                BookDescription book = bookDescriptionService.findByISNBVerified(s);
                userBookMark = new UserBookMark();
                if(book != null){
                    userBookMark.setBookDescription(book);
                    userBookMark.setUser(currentUser);
                    userBookMarkService.save(userBookMark);
                    added.add(s);
                }else{
                    // нет в бд, нет у пользователя - добавляем
                    book = BookParser.findByISBN(s);
                    if (book != null) {
                        book.setVerified(true);
                        bookDescriptionService.save(book);
                        userBookMark.setBookDescription(book);
                        userBookMark.setUser(currentUser);
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

        return "redirect:/books";
    }

    @PostMapping("/save")
    public String saveSingle(
            @Valid @ModelAttribute("userBook") UserBookDto userBook,
            BindingResult bindingResult,
            Model model, @AuthenticationPrincipal UserDetails auth) {

        if (bindingResult.hasErrors())
            return "books/addBook";

        User currentUser = userRepository.findByUsername(auth.getUsername()).get();
        BookDescription book = userBook.getBookDescription();
        book.setISBN(book.getISBN().replace("-", ""));

        // проверяем, есть ли у юзера книга с таким исбн
        UserBookMark userBookMark = userBookMarkService.getByUserIdAndISBN(currentUser.getId(), book.getISBN());
        if(userBookMark != null && !book.getISBN().isBlank()){
            model.addAttribute("errorMessage", "В вашей библиотеке уже есть книга с таким ISBN");
            return "books/addBook";
        }

        UserBookMark mark = userBook.getUserBookMark();
        mark.setUser(currentUser);

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
