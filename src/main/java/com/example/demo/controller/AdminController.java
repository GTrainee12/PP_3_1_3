package com.example.demo.controller;


import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public String findAll(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        List<User> users = userService.findAll();
        model.addAttribute("users", users.isEmpty() ? null : users);
        model.addAttribute("roles", roleRepository.findAll());
        return "bootstrap/admin/user-list";
    }
    @GetMapping("/profile")
    public String viewProfile(Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.findByEmail(username);
        if (user != null) {
            model.addAttribute("user", user);
            return "bootstrap/user/user-profile";
        } else {
            return "redirect:/logout";
        }
    }

    @GetMapping("/user-create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "bootstrap/admin/user-create";
    }

    @PostMapping("/user-create")
    public String createUser(@ModelAttribute User user, @RequestParam String password, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "bootstrap/admin/user-create";
        }
        userService.saveUser(user, password);
        redirectAttributes.addFlashAttribute("message", "User created successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/user-update")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam String password, Model model) {
        // Проверяем, если пароль не был изменен, то не обновляем его
        if (password != null && !password.isEmpty()) {
            userService.updateUser(user, password);
        } else {
            userService.updateUserWithoutPassword(user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/user-delete")
    public String deleteUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/admin/users";
    }
}

