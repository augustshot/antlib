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
import org.springframework.web.bind.annotation.*;
import ru.isu.antlib.model.User;
import ru.isu.antlib.repository.UserBookMarkRepository;
import ru.isu.antlib.repository.UserRepository;
import ru.isu.antlib.service.UserBookMarkService;
import ru.isu.antlib.service.UserService;

import java.util.List;

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

        int[] stats = userBookMarkService.getStats(user.getId());

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



}
