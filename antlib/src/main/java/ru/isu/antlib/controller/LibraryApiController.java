package ru.isu.antlib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.isu.antlib.model.*;
import ru.isu.antlib.repository.LibraryRepository;
import ru.isu.antlib.repository.UserRepository;
import ru.isu.antlib.service.*;

import java.util.*;

@RequestMapping("/libraries")
@RestController
public class LibraryApiController {
    @Autowired
    private UserLibraryService userLibraryService;
    @Autowired
    private LibraryService libraryService;
    @Autowired
    private UserService userService;

    @Autowired
    private UserBookMarkService userBookMarkService;

    @Autowired
    private RoomService roomService;
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private ShelfBookService shelfBookService;

    @PutMapping("/{id}")
    public Map<String, Object> updateLibrary(@PathVariable Integer id,
                                             @RequestBody Map<String, String> request,
                                             @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User currentUser = userService.getByUsername(auth.getUsername());

            Library library = libraryService.getById(id);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            // Проверка на владельца
            if (!userLibraryService.getOwner(library).getId().equals(currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Только владелец может редактировать библиотеку");
                return response;
            }

            String newName = request.get("name");

            library.setName(newName);
            libraryService.save(library, false);

            response.put("success", true);
            response.put("message", "Название библиотеки обновлено");
            response.put("name", library.getName());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

//    await fetch(`/libraries/${libraryId}/deleteLibrary`, { method: 'DELETE' });
    @DeleteMapping("/{libraryId}")
    public Map<String, Object> deleteLibrary(@PathVariable Integer libraryId,
                                          @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            Library library = libraryService.getById(libraryId);
            boolean isOwner = userLibraryService.getOwner(library).equals(user);
            if (!isOwner) {
                response.put("success", false);
                response.put("message", "У вас нет прав для удаления библиотеки");
                return response;
            }

            libraryService.deleteById(libraryId);

            response.put("success", true);
            response.put("message", "Библиотека удалена");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при удалении: " + e.getMessage());
        }

        return response;
    }


    @PostMapping("/addLibrary")
    public Map<String, Object> addLibrary(Model model,
                                          @RequestBody Map<String, String> request,
                                          @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            String name = request.get("name");

            boolean libraryExists = libraryService.existsByNameAndOwner(name, user);
            if (libraryExists) {
                response.put("success", false);
                response.put("message", "У вас уже есть библиотека с таким названием");
                return response;
            }

            Library library = new Library();
            library.setName(name);
            library = libraryService.save(library, true);
            UserLibrary userLibrary = new UserLibrary(user, library, Role.OWNER);
            userLibraryService.save(userLibrary);

            response.put("success", true);
            response.put("message", "Библиотека успешно создана");
            response.put("libraryId", library.getId());
            response.put("redirectUrl", "/libraries/library/" + library.getId());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при создании библиотеки: " + e.getMessage());

        }
        return response;
    }

    @PostMapping("/{libraryId}/regenerateCode")
    public Map<String, Object> regenerateInviteCode(@PathVariable Integer libraryId,
                                                    @AuthenticationPrincipal UserDetails auth) {

        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getByUsername(auth.getUsername());

            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            if (!userLibraryService.getOwner(library).getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "Только владелец может изменить код приглашения");
                return response;
            }

            libraryService.save(library, true);

            response.put("success", true);
            response.put("inviteCode", library.getInviteCode());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @DeleteMapping("/{libraryId}/removeMember")
    public Map<String, Object> removeMember(@PathVariable Integer libraryId,
                                            @RequestBody Map<String, Object> request,
                                            @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User currentUser = userService.getByUsername(auth.getUsername());
            String userIdStr = (String) request.get("userId");
            Integer userIdToRemove = userIdStr == null ? null : Integer.parseInt(userIdStr);
            String action = (String) request.get("action");

            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            if ("remove".equals(action) && !userLibraryService.getOwner(library).getId().equals(currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Только владелец может удалять участников");
                return response;
            }

            if(userIdToRemove == null){
                userIdToRemove = currentUser.getId();
            }

            UserLibrary userLibrary = userLibraryService.getByUserAndLibrary(userIdToRemove, libraryId);
            if (userLibrary == null) {
                response.put("success", false);
                response.put("message", "Пользователь не состоит в этой библиотеке");
                return response;
            }

            userLibraryService.delete(userLibrary);

            libraryService.save(library, false);

            response.put("success", true);
            response.put("message", "Участник удалён из библиотеки");

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка");
        }

        return response;
    }

    @PostMapping("/{libraryId}/addRoom")
    public Map<String, Object> addRoom(@PathVariable Integer libraryId,
                                       @RequestBody Map<String, String> request,
                                       @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            String name = request.get("name");

            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }


            Room room = new Room();
            room.setName(name.trim());
            room.setLibrary(library);

            boolean roomExists = roomService.exists(room);
            if (roomExists) {
                response.put("success", false);
                response.put("message", "Комната с таким названием уже существует");
                return response;
            }

            Room savedRoom = roomService.save(room);

            response.put("success", true);
            response.put("message", "Комната успешно создана");
            response.put("roomId", savedRoom.getId());
            response.put("roomName", savedRoom.getName());

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка при создании комнаты: " + e.getMessage());
        }

        return response;
    }


    @GetMapping("/{libraryId}/rooms/{roomId}")
    public Map<String, Object> getRoomContent(@PathVariable Integer libraryId,
                                              @PathVariable Integer roomId,
                                              @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            boolean isMember = userLibraryService.isMember(user, library);
            User owner = userLibraryService.getOwner(library);
            if (!isMember && !owner.getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа к этой библиотеке");
                return response;
            }

            Optional<Room> roomOpt = roomService.getById(roomId);
            if (roomOpt.isEmpty() || !roomOpt.get().getLibrary().getId().equals(libraryId)) {
                response.put("success", false);
                response.put("message", "Комната не найдена");
                return response;
            }
            Room room = roomOpt.get();

            // Получаем все полки комнаты
            List<Shelf> shelves = shelfService.getShelvesByRoomId(roomId);

            List<Map<String, Object>> shelvesData = new ArrayList<>();
            for (Shelf shelf : shelves) {
                Map<String, Object> shelfMap = new HashMap<>();
                shelfMap.put("id", shelf.getId());
                shelfMap.put("width", shelf.getWidth());
                shelfMap.put("height", shelf.getHeight());
                shelfMap.put("positionX", shelf.getPositionX());
                shelfMap.put("positionY", shelf.getPositionY());
                shelfMap.put("capacity", shelf.getCapacity());

                List<ShelfBook> shelfBooks = shelfBookService.getByShelfId(shelf.getId());
                List<Map<String, Object>> booksData = new ArrayList<>();

                for (ShelfBook shelfBook : shelfBooks) {
                        BookDescription book = shelfBook.getBookDescription();
                        Map<String, Object> bookMap = new HashMap<>();
                        bookMap.put("id", shelfBook.getId());
                        bookMap.put("title", book.getTitle());
                        bookMap.put("author", book.getAuthor());
                        bookMap.put("cover", book.getCover());
                        bookMap.put("position", shelfBook.getPosition());
                        booksData.add(bookMap);
                }
                shelfMap.put("books", booksData);
                shelvesData.add(shelfMap);
            }

            response.put("success", true);
            response.put("room", Map.of(
                    "id", room.getId(),
                    "name", room.getName()
            ));
            response.put("shelves", shelvesData);

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка при загрузке комнаты: " + e.getMessage());
        }

        return response;
    }

//    await fetch(`/libraries/${libraryId}/rooms/${roomId}/`, { method: 'DELETE' });
@DeleteMapping("/{libraryId}/rooms/{roomId}")
public Map<String, Object> deleteRoom(@PathVariable Integer libraryId,
                                           @PathVariable Integer roomId,
                                           @AuthenticationPrincipal UserDetails auth) {
    Map<String, Object> response = new HashMap<>();

    try {
        User user = userService.getByUsername(auth.getUsername());

        Library library = libraryService.getById(libraryId);
        boolean isOwner = userLibraryService.getOwner(library).equals(user);
        if (!isOwner) {
            response.put("success", false);
            response.put("message", "У вас нет прав для удаления комнаты");
            return response;
        }

        roomService.deleteById(roomId);

        response.put("success", true);
        response.put("message", "Комната удалена из библиотеки");

    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "Ошибка при удалении: " + e.getMessage());
    }

    return response;
}


    @GetMapping("/{libraryId}/shelves/{shelfId}")
    public Map<String, Object> getShelfBooks(@PathVariable Integer libraryId,
                                             @PathVariable Integer shelfId,
                                             @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Проверяем доступ пользователя к библиотеке
            User user = userService.getByUsername(auth.getUsername());

            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            boolean isMember = userLibraryService.isMember(user, library);
            User owner = userLibraryService.getOwner(library);
            if (!isMember && !owner.getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа к этой библиотеке");
                return response;
            }

            // Получаем полку
            Optional<Shelf> shelfOpt = shelfService.getById(shelfId);
            if (shelfOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Полка не найдена");
                return response;
            }

            // Получаем все ShelfBook для этой полки
            List<ShelfBook> shelfBooks = shelfBookService.getByShelfId(shelfId);

            List<Map<String, Object>> booksData = new ArrayList<>();
            for (ShelfBook shelfBook : shelfBooks) {
                    BookDescription book = shelfBook.getBookDescription();
                    // Находим UserBookMark для этого пользователя и книги
                    UserBookMark userBookMark = userBookMarkService.getByUserAndBookDescription(user, book);

                    Map<String, Object> bookMap = new HashMap<>();
                    bookMap.put("shelfBookId", shelfBook.getId());
                    bookMap.put("bookMarkId", userBookMark != null ? userBookMark.getId() : null);
                    bookMap.put("bookDescriptionId", book.getId());
                    bookMap.put("title", book.getTitle());
                    bookMap.put("author", book.getAuthor());
                    bookMap.put("cover", book.getCover());
                    bookMap.put("position", shelfBook.getPosition());
                    booksData.add(bookMap);
            }

            response.put("success", true);
            response.put("books", booksData);

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка при загрузке книг: " + e.getMessage());
        }

        return response;
    }

    @PutMapping("/{libraryId}/rooms/{roomId}")
    public Map<String, Object> updateRoom(@PathVariable Integer libraryId,
                                          @PathVariable Integer roomId,
                                          @RequestBody Map<String, String> request,
                                          @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            String newName = request.get("name");


            // Получаем библиотеку
            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            // Получаем комнату
            Optional<Room> roomOpt = roomService.getById(roomId);
            if (roomOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Комната не найдена");
                return response;
            }

            Room room = roomOpt.get();

            // Проверяем, что комната принадлежит библиотеке
            if (!room.getLibrary().getId().equals(libraryId)) {
                response.put("success", false);
                response.put("message", "Комната не принадлежит этой библиотеке");
                return response;
            }

            if(!newName.equals(room.getName())){
                room.setName(newName);

                // Проверяем, не существует ли уже комната с таким названием (исключая текущую)
                boolean exists = roomService.exists(room);
                if (exists) {
                    response.put("success", false);
                    response.put("message", "Комната с таким названием уже существует");
                    return response;
                }

                roomService.save(room);
            }

            response.put("success", true);
            response.put("message", "Комната успешно обновлена");
            response.put("roomId", room.getId());
            response.put("roomName", room.getName());

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка при обновлении комнаты: " + e.getMessage());
        }

        return response;
    }

    @PutMapping("/{libraryId}/rooms/{roomId}/saveShelves")
    public Map<String, Object> saveShelves(@PathVariable Integer libraryId,
                                           @PathVariable Integer roomId,
                                           @RequestBody Map<String, Object> request,
                                           @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Получаем текущего пользователя
            User user = userService.getByUsername(auth.getUsername());

            // Проверяем доступ к библиотеке
            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            // Только владелец может сохранять расстановку
            if (!userLibraryService.getOwner(library).getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "Только владелец библиотеки может изменять расстановку");
                return response;
            }

            // Проверяем, что комната существует и принадлежит библиотеке
            Optional<Room> roomOpt = roomService.getById(roomId);
            if (roomOpt.isEmpty() || !roomOpt.get().getLibrary().getId().equals(libraryId)) {
                response.put("success", false);
                response.put("message", "Комната не найдена");
                return response;
            }

            // Получаем данные из запроса
            List<Map<String, Object>> shelvesData = (List<Map<String, Object>>) request.get("shelves");
            List<Integer> deletedIds = (List<Integer>) request.get("deletedIds");

            // Удаляем помеченные полки
            if (deletedIds != null && !deletedIds.isEmpty()) {
                for (Integer id : deletedIds) {
                    shelfService.deleteById(id);
                }
            }

            // Сохраняем или обновляем полки
            if (shelvesData != null) {
                for (Map<String, Object> shelfData : shelvesData) {
                    Integer id = (Integer) shelfData.get("id");
                    Integer width = (Integer) shelfData.get("width");
                    Integer height = (Integer) shelfData.get("height");
                    Integer positionX = (Integer) shelfData.get("positionX");
                    Integer positionY = (Integer) shelfData.get("positionY");
                    Integer capacity = (Integer) shelfData.get("capacity");

                    Shelf shelf;
                    if (id != null && id > 0) {
                        // Обновление существующей полки
                        shelf = shelfService.getById(id)
                                .orElseThrow(() -> new RuntimeException("Полка не найдена: " + id));
                        shelf.setWidth(width);
                        shelf.setHeight(height);
                        shelf.setPositionX(positionX);
                        shelf.setPositionY(positionY);
                        shelf.setCapacity(capacity);
                    } else {
                        // Создание новой полки
                        shelf = new Shelf();
                        shelf.setRoom(roomOpt.get());
                        shelf.setWidth(width);
                        shelf.setHeight(height);
                        shelf.setPositionX(positionX);
                        shelf.setPositionY(positionY);
                        shelf.setCapacity(capacity);
                    }
                    shelfService.save(shelf);
                }
            }

            response.put("success", true);
            response.put("message", "Расстановка сохранена");

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка при сохранении расстановки: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/{libraryId}/books")
    public Map<String, Object> getUserBooks(@PathVariable Integer libraryId,
                                            @RequestParam(required = false) String search,
                                            @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            // Проверяем доступ к библиотеке
            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            // Проверяем, является ли пользователь участником библиотеки
            boolean isMember = userLibraryService.isMember(user, library);
            if (!isMember && !userLibraryService.getOwner(library).getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа к этой библиотеке");
                return response;
            }

            // Поиск книг пользователя со статусом OWNED
            List<UserBookMark> userBooks;
            if (search != null && !search.trim().isEmpty()) {
                String searchTerm = "%" + search.trim().toLowerCase() + "%";
                userBooks = userBookMarkService.getByUserAndSourceAndSearch(
                        user, Source.OWNED, searchTerm);
            } else {
                userBooks = userBookMarkService.getByUserAndSource(user, Source.OWNED);
            }


            // Формируем ответ
            List<Map<String, Object>> booksData = new ArrayList<>();
            for (UserBookMark ubm : userBooks) {
                BookDescription book = ubm.getBookDescription();
                Map<String, Object> bookMap = new HashMap<>();
                bookMap.put("bookMarkId", ubm.getId());
                bookMap.put("title", book.getTitle());
                bookMap.put("author", book.getAuthor());
                bookMap.put("isbn", book.getISBN());
                bookMap.put("cover", book.getCover());
                bookMap.put("year", book.getYear());
                booksData.add(bookMap);
            }

            response.put("success", true);
            response.put("books", booksData);

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка при загрузке книг: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/{libraryId}/shelves/{shelfId}/addBooks")
    public Map<String, Object> addBooksToShelf(@PathVariable Integer libraryId,
                                               @PathVariable Integer shelfId,
                                               @RequestBody Map<String, List<Integer>> request,
                                               @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            // Проверка доступа к библиотеке
            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            boolean isMember = userLibraryService.isMember(user, library);
            if (!isMember && !userLibraryService.getOwner(library).getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа к этой библиотеке");
                return response;
            }

            // Проверка существования полки
            Shelf shelf = shelfService.getById(shelfId)
                    .orElseThrow(() -> new RuntimeException("Полка не найдена"));

            if (!shelf.getRoom().getLibrary().getId().equals(libraryId)) {
                response.put("success", false);
                response.put("message", "Полка не принадлежит этой библиотеке");
                return response;
            }

            List<Integer> bookMarkIds = request.get("bookMarkIds");
            if (bookMarkIds == null || bookMarkIds.isEmpty()) {
                response.put("success", false);
                response.put("message", "Не выбраны книги для добавления");
                return response;
            }

            int currentBookCount = shelfBookService.countBooks(shelf);
            int availableSpace = shelf.getCapacity() - currentBookCount;
//            System.out.println("current: " + currentBookCount);
//            System.out.println("capacity: " + shelf.getCapacity());
//            System.out.println("available: " + availableSpace);
//            System.out.println("adding: " + bookMarkIds.size());
//            System.out.println("fits? " + (bookMarkIds.size() <= availableSpace));

            if (bookMarkIds.size() > availableSpace) {
                response.put("success", false);
                response.put("message", String.format("На полке нет места",
                        availableSpace, bookMarkIds.size()));
                return response;
            }

            for (Integer bookMarkId : bookMarkIds) {
                UserBookMark userBookMark = userBookMarkService.getByUserBookMarkId(bookMarkId);

                ShelfBook shelfBook = new ShelfBook();
                shelfBook.setShelf(shelf);
                shelfBook.setBookDescription(userBookMark.getBookDescription());
                shelfBook.setPosition(currentBookCount+1);  // порядковый номер на полке
                shelfBookService.save(shelfBook);
                currentBookCount = shelfBookService.countBooks(shelf);
            }

            response.put("success", true);
            response.put("message", "Книги добавлены на полку");

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", "Ошибка при добавлении книг: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/{libraryId}/books/search")
    public Map<String, Object> searchBooksInLibrary(@PathVariable Integer libraryId,
                                                    @RequestParam String query,
                                                    @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            Library library = libraryService.getById(libraryId);
            if (library == null) {
                response.put("success", false);
                response.put("message", "Библиотека не найдена");
                return response;
            }

            boolean isMember = userLibraryService.isMember(user, library);
            if (!isMember && !userLibraryService.getOwner(library).getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "У вас нет доступа");
                return response;
            }

            String searchPattern = "%" + query.toLowerCase() + "%";

            List<Map<String, Object>> books = shelfBookService.searchLibrary(library, searchPattern);

            response.put("success", true);
            response.put("books", books);

        } catch (Exception e) {
            
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }



//    await fetch(`/libraries/${libraryId}/deleteShelfBook/${id}`, { method: 'DELETE' });
    @DeleteMapping("/{libraryId}/shelfBook/{shelfBookId}")
    public Map<String, Object> deleteShelfBook(@PathVariable Integer libraryId,
                                               @PathVariable Integer shelfBookId,
                                               @AuthenticationPrincipal UserDetails auth) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.getByUsername(auth.getUsername());

            Library library = libraryService.getById(libraryId);
            boolean isOwner = userLibraryService.getOwner(library).equals(user);
            if (!isOwner) {
                response.put("success", false);
                response.put("message", "У вас нет прав для удаления книги с полки");
                return response;
            }

            shelfBookService.deleteById(shelfBookId);

            response.put("success", true);
            response.put("message", "Книга удалена с полки");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка при удалении: " + e.getMessage());
        }

        return response;
    }


}



