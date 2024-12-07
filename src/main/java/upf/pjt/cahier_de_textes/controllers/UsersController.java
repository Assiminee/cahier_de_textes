package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import upf.pjt.cahier_de_textes.dto.UserRegistrationDto;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.models.CustomUserDetails;
import upf.pjt.cahier_de_textes.services.UserRegistrationService;
import upf.pjt.cahier_de_textes.services.UserService;

import java.util.List;
import java.util.UUID;

@Controller
public class UsersController {
    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private UserService userService;
    @GetMapping("/Admin/users")
    public String showUsers(Model model) {
        // Fetch the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser); // Pass the user to the model
        }

        // Fetch all users for the table
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users); // Pass users list to the model

        return "Admin/users";
    }



    @PostMapping("/Admin/add")
    public String addUser(@Validated @ModelAttribute UserRegistrationDto userRegistrationDto, Model model) {
        try {
            userRegistrationService.registerUser(userRegistrationDto);
            model.addAttribute("successMessage", "User added successfully");
            return "redirect:/Admin/users"; // Redirect to the user list or a success page
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "Admin/addUserForm"; // Show the form again with an error message
        }
    }

    @PostMapping("/Admin/delete/{id}")
    public String deleteUser(@PathVariable UUID id, Model model) {
        try {
            userService.DeleteUserById(id);
            model.addAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/Admin/users"; // Redirect to the user list after deletion
    }


}
