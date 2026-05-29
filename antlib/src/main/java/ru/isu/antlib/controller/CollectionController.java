package ru.isu.antlib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.isu.antlib.model.*;
import ru.isu.antlib.model.Collection;
import ru.isu.antlib.repository.CollectionBookRepository;
import ru.isu.antlib.repository.CollectionRepository;
import ru.isu.antlib.service.CollectionBookService;
import ru.isu.antlib.service.CollectionService;
import ru.isu.antlib.service.UserBookMarkService;
import ru.isu.antlib.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/collections")
public class CollectionController {
    @Autowired
    private UserService userService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionBookService collectionBookService;
    @Autowired
    private CollectionBookRepository collectionBookRepository;

    @Autowired
    private UserBookMarkService userBookMarkService;

    @GetMapping
    public String collections(
            Model model,
            @AuthenticationPrincipal UserDetails auth,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir,
            Pageable page) {

        User user = userService.getByUsername(auth.getUsername());

        String sortField = "lastUpdate";
        Sort.Direction direction = Sort.Direction.DESC;

        if (sort != null && !sort.isEmpty()) {
            sortField = sort;
            direction = (dir != null && dir.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page.getPageNumber(), 12, Sort.by(direction, sortField));

        Page<Collection> pageResult = collectionRepository.findByUser(user, pageable);

        model.addAttribute("sort", sortField);
        model.addAttribute("dir", direction.toString().toLowerCase());
        model.addAttribute("page", pageResult);

        return "collections/allCollections";
    }

    @ResponseBody
    @PostMapping("/addCollection")
    public Map<String, Object> addCollection(Model model,
                                          @RequestBody Map<String, String> request,
                                          @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            String name = request.get("name");
            String description = request.get("description");

            boolean collectionExists = collectionService.existsByNameAndUser(name, user);
            if (collectionExists) {
                response.put("success", false);
                response.put("message", "У вас уже есть библиотека с таким названием");
                return response;
            }

            Collection collection = new Collection();
            collection.setName(name);
            collection.setDescription(description);
            collection.setUser(user);
            collection = collectionService.save(collection);

            response.put("success", true);
            response.put("message", "Коллекция успешно создана");
            response.put("collectionId", collection.getId());
            response.put("redirectUrl", "/collections/collection/" + collection.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при создании коллекции");

        }
        return response;
    }

    @GetMapping("/collection/{id}")
    public String collection(Model model, @PathVariable Integer id, @AuthenticationPrincipal UserDetails auth,
                             @RequestParam(required = false) String sort,
                             @RequestParam(required = false) String dir,
                             Pageable page){
        User user = userService.getByUsername(auth.getUsername());

        Collection collection = collectionService.getById(id).orElse(null);
        if(collection == null){
            return "error/404";
        }

        boolean isOwner = collection.getUser().equals(user);
        if (!isOwner) {
            return "error/403";
        }

        String sortField = "userBookMark.bookDescription.title";
        Sort.Direction direction = Sort.Direction.ASC;

        if (sort != null && !sort.isEmpty()) {
            sortField = sort;
            direction = (dir != null && dir.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page.getPageNumber(), 30, Sort.by(direction, sortField));

        Page<UserBookMark> pageResult = collectionBookRepository.findByCollectionId(id, pageable);

        model.addAttribute("sort", sortField);
        model.addAttribute("dir", direction.toString().toLowerCase());
        model.addAttribute("page", pageResult);
        model.addAttribute("collection", collection);

        return "collections/collection";
    }

//    await fetch(`/collections/collection/${collectionId}
    @ResponseBody
    @PutMapping("/collection/{id}")
    public Map<String, Object> updateCollection(@PathVariable Integer id,
                                             @RequestBody Map<String, String> request,
                                             @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User currentUser = userService.getByUsername(auth.getUsername());

            Collection collection = collectionService.getById(id).orElse(null);
            if (collection == null) {
                response.put("success", false);
                response.put("message", "Коллекция не найдена");
                return response;
            }

            if (!collection.getUser().getId().equals(currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Вы не можете редактировать эту коллекцию");
                return response;
            }

            String newName = request.get("name");
            String newDescription = request.get("description");

            collection.setName(newName);
            collection.setDescription(newDescription);
            collectionService.save(collection);

            response.put("success", true);
            response.put("message", "Коллекция обновлена");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    //    await fetch(`/collections/collection/${collectionId}`
    @ResponseBody
    @DeleteMapping("collection/{id}")
    public Map<String, Object> deleteCollection(@PathVariable Integer id,
                                             @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();
    
        try {
            User user = userService.getByUsername(auth.getUsername());
    
            Collection collection = collectionService.getById(id).orElse(null);
            if (collection == null) {
                response.put("success", false);
                response.put("message", "Коллекция не найдена");
                return response;
            }
            boolean isOwner = collection.getUser().equals(user);
            if (!isOwner) {
                response.put("success", false);
                response.put("message", "У вас нет прав для удаления коллекции");
                return response;
            }
    
            collectionService.deleteById(id);
    
            response.put("success", true);
            response.put("message", "Коллекция удалена");
    
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при удалении: " + e.getMessage());
        }
    
        return response;
    }


    @ResponseBody
    @GetMapping("/{collectionId}/books")
    public Map<String, Object> getAvailableBooksForCollection(@PathVariable Integer collectionId,
            @RequestParam(required = false) String search, @AuthenticationPrincipal UserDetails auth) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            Collection collection = collectionService.getById(collectionId).orElse(null);
            if (collection == null) {
                response.put("success", false);
                response.put("message", "Коллекция не найдена");
                response.put("errorCode", 404);
                return response;
            }

            if (!collection.getUser().getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа к этой коллекции");
                response.put("errorCode", 403);
                return response;
            }

            Set<Integer> existingBookIds = collectionBookService.getAllByCollectionId(collectionId)
                    .stream()
                    .map(book -> book.getId())
                    .collect(Collectors.toSet());
            List<UserBookMark> allBooks;
            if (search != null && !search.trim().isEmpty()) {
                allBooks = userBookMarkService.getDistinctByUserAndSearch(user, "%" + search + "%");
            } else {
                allBooks = userBookMarkService.getAllByUser(user);
            }

            List<UserBookMark> available = allBooks
                    .stream()
                    .filter(book -> !existingBookIds.contains(book.getId()))
                    .toList();

            List<Map<String, Object>> books = new ArrayList<>();
            for (UserBookMark userBookMark : available) {
                BookDescription book = userBookMark.getBookDescription();
                Map<String, Object> bookMap = new HashMap<>();
                bookMap.put("id", userBookMark.getId());
                bookMap.put("title", book.getTitle());
                bookMap.put("author", book.getAuthor());
                bookMap.put("year", book.getYear());
                bookMap.put("isbn", book.getISBN());
                books.add(bookMap);
            }

            response.put("success", true);
            response.put("books", books);
            response.put("totalCount", books.size());

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Ошибка при загрузке книг: " + e.getMessage());
        }

        return response;
    }

    //    await fetch(`/collections/collection/${collectionId}/books`
    @ResponseBody
    @PostMapping("/collection/{collectionId}/books")
    public Map<String, Object> addBooksToShelf(@PathVariable Integer collectionId,
                                               @RequestBody Map<String, List<Integer>> request,
                                               @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            Collection collection = collectionService.getById(collectionId).orElse(null);
            if (collection == null) {
                response.put("success", false);
                response.put("message", "Коллекция не найдена");
                return response;
            }

            if (!collection.getUser().getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа к этой коллекции");
                response.put("errorCode", 403);
                return response;
            }

            List<Integer> bookIds = request.get("bookIds");
            if (bookIds == null || bookIds.isEmpty()) {
                response.put("success", false);
                response.put("message", "Не выбраны книги для добавления");
                return response;
            }


            for (Integer bookMarkId : bookIds) {
                UserBookMark userBookMark = userBookMarkService.getById(bookMarkId);
                CollectionBook collectionBook = new CollectionBook(collection, userBookMark);
                collectionBookService.save(collectionBook);
                collectionService.save(collection);
            }

            response.put("success", true);
            response.put("message", "Книги добавлены в коллекцию");

        } catch (Exception e) {

            response.put("success", false);
            response.put("message", "Ошибка при добавлении книг");
        }

        return response;
    }


//    await fetch(`/collections/collection/${collectionId}/books/${bookToRemove}`,
    @ResponseBody
    @DeleteMapping("/collection/{collectionId}/books/{bookId}")
    public Map<String, Object> deleteShelfBook(@PathVariable Integer bookId,
                                               @PathVariable Integer collectionId,
                                               @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());
            Collection collection = collectionService.getById(collectionId).orElse(null);
            if (collection == null) {
                response.put("success", false);
                response.put("message", "Коллекция не найдена");
                return response;
            }

            if (!collection.getUser().getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа к этой коллекции");
                response.put("errorCode", 403);
                return response;
            }

            CollectionBook collectionBook = collectionBookService.getByCollectionIdAndBookId(collectionId, bookId);

            collectionBookService.delete(collectionBook);

            response.put("success", true);
            response.put("message", "Книга удалена из коллекции");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при удалении книги");
        }

        return response;
    }

}


