package ru.isu.antlib.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalControllerExceptionHandler implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest req, Model model) {
        Object status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer code = Integer.valueOf(status.toString());

            String errorMessage = (String) req.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            String requestUri = (String) req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            Throwable exception = (Throwable) req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

            model.addAttribute("status", code);
            model.addAttribute("message", errorMessage != null ? errorMessage : "");
            model.addAttribute("path", requestUri);

            if (exception != null) {
                model.addAttribute("exception", exception.getClass().getSimpleName());
                model.addAttribute("exceptionMessage", exception.getMessage());
            }

            return switch (code) {
                case 404 -> "error/404";
                case 403 -> "error/403";
                case 400 -> "error/400";
                case 500 -> "error/500";
                default -> "error/error";
            };
        }
        return "";
    }
}