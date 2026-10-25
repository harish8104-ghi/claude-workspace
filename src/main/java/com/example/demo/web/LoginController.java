package com.example.demo.web;

import com.example.demo.auth.UserAccountService;
import com.example.demo.auth.UserAccountService.RegistrationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserAccountService userAccountService;

    public LoginController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registrationPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        RegistrationResult result = userAccountService.register(username, password);
        if (result == RegistrationResult.CREATED) {
            return "redirect:/login?registered";
        }
        redirectAttributes.addAttribute("error", result.name().toLowerCase());
        return "redirect:/register";
    }

    @ExceptionHandler(DataAccessException.class)
    public String registrationDatabaseError(DataAccessException exception, Model model) {
        logger.error("Could not create registered user account", exception);
        model.addAttribute("registrationError", true);
        return "register";
    }
}
