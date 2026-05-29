package ru.isu.antlib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.isu.antlib.model.Source;
import ru.isu.antlib.model.Status;
import ru.isu.antlib.model.User;
import ru.isu.antlib.model.UserBookMark;
import ru.isu.antlib.repository.UserRepository;
import ru.isu.antlib.service.UserBookMarkService;
import ru.isu.antlib.service.UserService;
import ru.isu.antlib.service.report.ExcelBooksReport;
import ru.isu.antlib.service.report.ExcelStatsReport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserBookMarkService userBookMarkService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;


    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal UserDetails auth){
        User user = userService.getByUsername(auth.getUsername());
        model.addAttribute("user", user);

        int[] stats = userBookMarkService.getStatusStats(user.getId());

        model.addAttribute("planned", stats[0]);
        model.addAttribute("reading", stats[1]);
        model.addAttribute("read", stats[2]);
        model.addAttribute("postponed", stats[3]);

        return "user/profile";
    }

    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal UserDetails auth){
        User user = userService.getByUsername(auth.getUsername());
        model.addAttribute("user", user);
        return "user/editProfile";
    }

    @PostMapping("/edit")
    public String update(Model model, @AuthenticationPrincipal UserDetails auth,
                         @ModelAttribute("user") User edited, @CurrentSecurityContext SecurityContext securityContext){
        User user = userService.getByUsername(auth.getUsername());
        user.setUsername(edited.getUsername());
        user.setEmail(edited.getEmail());

        userService.update(user, edited.getPassword());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());

        securityContext.setAuthentication(authentication);

        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String delete(Model model, @AuthenticationPrincipal UserDetails auth){
        User user = userService.getByUsername(auth.getUsername());
        userService.delete(user);
        return "redirect:/logout";
    }

    @GetMapping("/downloadReport/books")
    public ModelAndView booksReport(Model model, @AuthenticationPrincipal UserDetails auth) {
        User user = userService.getByUsername(auth.getUsername());
        List<UserBookMark> userBookMarks = userBookMarkService.getAllByUser(user);

        ModelAndView view = new ModelAndView(new ExcelBooksReport());
        view.addObject("userBookMarks", userBookMarks);
        view.addObject("username", user.getUsername());
        return view;
    }

    @GetMapping("/downloadReport/stats")
    public ModelAndView statsReport(Model model, @AuthenticationPrincipal UserDetails auth) {
        User user = userService.getByUsername(auth.getUsername());
        Integer userId = user.getId();

        ModelAndView view = new ModelAndView(new ExcelStatsReport());

        List<String> statuses = Arrays.asList(
                Status.PLANNED.getValue(),
                Status.READING.getValue(),
                Status.FINISHED.getValue(),
                Status.POSTPONED.getValue(),
                "Не указан"
        );
        Map<String, Integer> status = new HashMap<>();
        int[] statusStats = userBookMarkService.getStatusStats(userId);
        for(int i = 0; i<5; i++) {
            status.put(statuses.get(i), statusStats[i]);
        }

        List<String> sources = Arrays.asList(
                Source.OWNED.getValue(),
                Source.EBOOK.getValue(),
                Source.BORROWED.getValue(),
                Source.SHARED.getValue(),
                "Не указан"
        );
        Map<String, Integer> source = new HashMap<>();
        int[] sourceStats = userBookMarkService.getSourceStats(userId);
        for(int i = 0; i<5; i++) {
            source.put(sources.get(i), sourceStats[i]);
        }

        List<Object[]> languageStats = userBookMarkService.getLanguageStats(userId);
        Map<String, Long> language = new HashMap<>();
        for (Object[] row : languageStats) {
            String lang = (String) row[0];
            Long count = (Long) row[1];
            language.put(lang != null && !lang.isEmpty() ? lang : "Не указан", count);
        }

//        1) статистика по статусам + диаграмма
        view.addObject("status", status);
//        2) статистика по источникам + диаграмма
        view.addObject("source", source);
//        3) статистика по языкам (язык - кол-во)
        view.addObject("language", language);
//        4) общее кол-во страниц в прочитанных книгах
        view.addObject("pages", userBookMarkService.getTotalPages(userId));
//        5) средний рейтинг
        view.addObject("rating", userBookMarkService.getAverageRating(userId));
//        6) топ-5 книг по рейтингу
//        7) топ-5 авторов

        view.addObject("username", user.getUsername());
        return view;
    }




}
