package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.dtos.UserRegistrationDto;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.dtos.EditUserDTO;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import upf.pjt.cahier_de_textes.services.UserRegistrationService;
import upf.pjt.cahier_de_textes.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(name = "User management endpoints", path = "/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfesseurRepository professeurRepository;
    private final UserRegistrationService userRegistrationService;


    @Autowired
    public UserController(UserRepository userRepository, ProfesseurRepository professeurRepository, UserRegistrationService userRegistrationService, UserService userService) {
        this.userRepository = userRepository;
        this.professeurRepository = professeurRepository;
        this.userRegistrationService = userRegistrationService;
        this.userService = userService;
    }

    @GetMapping
    public String showUsers(Model model) {
        // Fetch the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser);
            model.addAttribute("grades", Grade.values());
            model.addAttribute("roles", RoleEnum.values());
            model.addAttribute("genres", Genre.values());// Pass the user to the model
        }

        // Fetch all users for the table
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users); // Pass users list to the model

        return "Admin/users";
    }

    @PostMapping()
    public String addUser(@Validated @ModelAttribute UserRegistrationDto userRegistrationDto, Model model, RedirectAttributes redAtt) {
        try {
            userRegistrationService.registerUser(userRegistrationDto);
            redAtt.addFlashAttribute("action", true);
            redAtt.addFlashAttribute("added", userRegistrationDto.getNom() + " " + userRegistrationDto.getPrenom());
            return "redirect:/users"; // Redirect to the user list or a success page
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "Admin/users"; // Show the form again with an error message
        }
    }

    @PutMapping(path = "/{id}")
    public String editUser(@PathVariable("id") String id, @ModelAttribute EditUserDTO incomingUser, Model model, RedirectAttributes redirectAttributes) {
        UUID convertedId = UUID.fromString(String.valueOf(id));
        User user = userRepository.findById(convertedId).orElse(null);

        if (user == null)
            return "redirect:/auth/login";

        if (!userService.hasUniqueAttributes(user, incomingUser, redirectAttributes)) {
//            profileController.profile(model);
            return "redirect:/profile";
        }

        if (incomingUser.getRole().name().equals("ROLE_PROF")) {
            Optional<Professeur> prof = professeurRepository.findById(convertedId);

            if (prof.isEmpty())
                return "redirect:/auth/login";

            Professeur professeur = prof.get();
            incomingUser.setUserDetails(professeur);
            professeurRepository.save(professeur);
            model.addAttribute("user", professeur);
            return "profile/profile";
        }

        Optional<User> optionalUser = userRepository.findById(convertedId);

        if (optionalUser.isEmpty())
            return "redirect:/auth/login";;

        User loggedUser = optionalUser.get();
        incomingUser.setUserDetails(loggedUser);
        userRepository.save(loggedUser);
        model.addAttribute("user", loggedUser);

        return "profile/profile";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null)
                return "redirect:/users";

            String nom = user.getNom();
            String prenom = user.getPrenom();

            userService.deleteUser(id);

            redirectAttributes.addFlashAttribute("action", true);
            redirectAttributes.addFlashAttribute("deleted", nom + " " + prenom);
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
