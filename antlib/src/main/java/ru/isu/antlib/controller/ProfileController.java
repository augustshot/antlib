package ru.isu.antlib.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.isu.antlib.model.User;
import ru.isu.antlib.repository.UserRepository;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal UserDetails auth){
        User user = userRepository.findByUsername(auth.getUsername()).get();
        model.addAttribute("user", user);

        model.addAttribute("planned", 0);
        model.addAttribute("reading", 0);
        model.addAttribute("read", 0);
        model.addAttribute("postponed", 0);

        return "user/profile";
    }

    @ResponseBody
    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal UserDetails auth){
        return "user/edit";
    }


}
