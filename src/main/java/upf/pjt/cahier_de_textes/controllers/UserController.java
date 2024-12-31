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
import upf.pjt.cahier_de_textes.dao.dtos.EditPwdDTO;
import upf.pjt.cahier_de_textes.dao.dtos.UserRegistrationDto;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.QualificationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import upf.pjt.cahier_de_textes.services.UserDetailsServiceImpl;
import upf.pjt.cahier_de_textes.services.UserRegistrationService;
import upf.pjt.cahier_de_textes.services.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(name = "User management endpoints", path = "/users")
public class UserController {
    private final UserService userService;
    private final QualificationRepository qualificationRepository;
    private UserRepository userRepository;
    private ProfesseurRepository professeurRepository;
    private UserRegistrationService userRegistrationService;


    @Autowired
    public UserController(UserRepository userRepository, ProfesseurRepository professeurRepository, UserRegistrationService userRegistrationService, UserService userService, QualificationRepository qualificationRepository) {
        this.userRepository = userRepository;
        this.professeurRepository = professeurRepository;
        this.userRegistrationService = userRegistrationService;
        this.userService = userService;
        this.qualificationRepository = qualificationRepository;
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

    @PatchMapping("/{id}")
    public String editProfileInformation(@PathVariable("id") UUID id, @ModelAttribute UserDTO incomingUser, RedirectAttributes redirectAttributes) {
        try {
            boolean userExists = userRepository.existsById(id);

            if (!userExists) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                redirectAttributes.addFlashAttribute("success", false);
                return "redirect:/auth/login?error";
            }

            User user = userRepository.findById(id).orElse(null);

            if (userService.hasUniqueAttributes(id, incomingUser, redirectAttributes)) {
                assert user != null;
                incomingUser.setUserDetails(user);

                if (user.getRole().getAuthority().equals("ROLE_PROF"))
                    incomingUser.setProfessorDetails(qualificationRepository, professeurRepository);

                userRepository.save(user);
                UserDetailsServiceImpl.updateCustomUserDetails(user);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("success", false);
        }

        return "redirect:/profile";
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

    @PutMapping("/{id}/password")
    public String updatePassword(@PathVariable UUID id, @ModelAttribute EditPwdDTO editPwdDTO, RedirectAttributes redAtts) throws Exception {
        System.out.println(editPwdDTO);
        User user = userRepository.findById(id).orElse(null);

        System.out.println(user);

        if (user == null)
            throw new Exception("User with ID '" + id + "' not found");

        String message;

        try {
            boolean correctPassword = userService.correctPassword(editPwdDTO.getOldPassword(), user.getPwd());

            if (correctPassword) {
                user.setPwd(userService.getEncoder().encode(editPwdDTO.getNewPassword()));
                userRepository.save(user);
            }

            System.out.println(user);

            message = correctPassword ? "Le mot de passe a bien été modifié" : "L'ancien mot de passe fourni est incorrecte";
        } catch (Exception e) {
            message = "Impossible de changer de mot de passe. Veuillez reessayer plus tard.";
        }

        redAtts.addFlashAttribute("password", message);
        return "redirect:/profile";
    }
}
