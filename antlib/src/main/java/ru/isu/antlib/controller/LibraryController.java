package ru.isu.antlib.controller;

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.isu.antlib.model.*;
import ru.isu.antlib.repository.LibraryRepository;
import ru.isu.antlib.repository.UserLibraryRepository;
import ru.isu.antlib.repository.UserRepository;
import ru.isu.antlib.service.LibraryService;
import ru.isu.antlib.service.RoomService;
import ru.isu.antlib.service.UserLibraryService;
import ru.isu.antlib.service.UserService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/libraries")
@RequiredArgsConstructor
public class LibraryController {
    @Autowired
    private UserService userService;
    @Autowired
    private LibraryRepository libraryRepository;
    @Autowired
    private UserLibraryService userLibraryService;
    @Autowired
    private LibraryService libraryService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @GetMapping
    public String libraries(Model model,
                            @AuthenticationPrincipal UserDetails auth){

        User user = userService.getByUsername(auth.getUsername());
        List<UserLibrary> userLibraries = userLibraryService.getAllByUser(user);

        Map<UserLibrary, Integer> userLibrariesMap = new LinkedHashMap<>();
        userLibraries.forEach(l -> userLibrariesMap.put(l, userLibraryService.memberCount(l.getLibrary().getId())));

        model.addAttribute("userLibraries", userLibrariesMap);

        return "libraries/allLibraries";
    }

    @PostMapping("/join")
    public String join(Model model, @RequestParam("inviteCode") String inviteCode,
                       @AuthenticationPrincipal UserDetails auth,
                       RedirectAttributes redirectAttributes){
        User user = userService.getByUsername(auth.getUsername());

        Library library = libraryService.getByInviteCode(inviteCode);
        if(library != null){
            if(userLibraryService.isMember(user, library)){
                redirectAttributes.addFlashAttribute("errorMessage", "Вы уже являетесь участником данной библиотеки: " + library.getName());
                return "redirect:/libraries";
            }
            UserLibrary userLibrary = new UserLibrary(user, library, Role.MEMBER);
            userLibraryService.save(userLibrary);
            libraryService.save(library, false);
            return "redirect:/libraries/library/" + library.getId();
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Такой библиотеки не существует");
        return "redirect:/libraries";
    }

    @GetMapping("/library/{id}")
    public String library(Model model, @PathVariable Integer id, @AuthenticationPrincipal UserDetails auth) throws Exception {
        User user = userService.getByUsername(auth.getUsername());
        Library library = libraryService.getById(id);
        List<UserLibrary> members = userLibraryService.findByLibraryId(id);
        List<Room> rooms = roomService.getRoomsByLibraryId(library.getId());

        boolean isMember = userLibraryService.isMember(user, library);
        if (!isMember) {
            return "error/403";
        }

        model.addAttribute("library", library);
        model.addAttribute("isOwner", userLibraryService.getOwner(library).getId() == user.getId());
        model.addAttribute("members", members);
        model.addAttribute("rooms", rooms);
        return "libraries/library";
    }



}
