package com.example.demo.controller;


import com.example.demo.controller.error.EmailAlreadyExistsException;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "bootstrap/security/register";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") User user, Model model,
                               @RequestParam String password, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUser(user, password);
            redirectAttributes.addFlashAttribute("message", "User registered successfully, Please log in.");
            return "redirect:/login";
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("errorMessage", "Email is already registered.");
            model.addAttribute("roles", roleRepository.findAll());
            return "bootstrap/security/register";
        }
    }
}

