package ru.isu.antlib.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.isu.antlib.model.User;
import ru.isu.antlib.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping(value="/login")
    public String goLogin(){
        return "login";
    }

    @GetMapping("/checkUsername")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = userService.existsByUsername(username);
        response.put("exists", exists);
        return response;
    }

    @GetMapping(value = "/register")
    public String goRegister() {
        return "register";
    }


    @PostMapping(value = "/register")
    public String register(@ModelAttribute User user, HttpServletRequest request,
                           @CurrentSecurityContext SecurityContext securityContext,
                           RedirectAttributes redirectAttributes) {

        userService.save(user);

        //Program authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());

        securityContext.setAuthentication(authentication);

        // Create a new session and add the security context.
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        return "redirect:/books";
    }
}
