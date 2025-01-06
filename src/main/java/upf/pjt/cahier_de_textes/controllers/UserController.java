package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.dtos.EditPwdDTO;
import upf.pjt.cahier_de_textes.dao.dtos.UserRegistrationDto;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.dtos.EditUserDTO;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.RoleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import upf.pjt.cahier_de_textes.services.UserRegistrationService;
import upf.pjt.cahier_de_textes.services.UserService;

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
    private final RoleRepository roleRepository;



    @Autowired
    public UserController(UserRepository userRepository, ProfesseurRepository professeurRepository, UserRegistrationService userRegistrationService, UserService userService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.professeurRepository = professeurRepository;
        this.userRegistrationService = userRegistrationService;
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String showUsers(
            Model model,
            @RequestParam(required = false) String nomComplet,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Authenticate and set model attributes
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser);
            model.addAttribute("grades", Grade.values());
            model.addAttribute("roles", RoleEnum.values());
            model.addAttribute("genres", Genre.values());
        }
        model.addAttribute("nomComplet", nomComplet);
        model.addAttribute("role", role);
        model.addAttribute("email", email);

        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());

        Role profRole = roleRepository.findByRole(RoleEnum.ROLE_PROF);
        Role roleEntity = null;
        if (role != null && !role.isBlank()) {
            try {
                RoleEnum roleEnum = RoleEnum.valueOf(role.toUpperCase());
                if (roleEnum != RoleEnum.ROLE_PROF) { // Exclude 'prof'
                    roleEntity = roleRepository.findByRole(roleEnum);
                } else {
                    model.addAttribute("error", "Cannot filter by 'prof' role.");
                }
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Invalid role");
            }
        }
        Page<User> usersPage = userRepository.searchUsers(nomComplet, email, roleEntity, profRole, pageable);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        return "Admin/User/users";
    }

    @PostMapping()
    public String addUser(@Validated @ModelAttribute UserRegistrationDto userRegistrationDto, Model model, RedirectAttributes redAtt) {

        try {
            userRegistrationService.registerUser(userRegistrationDto);
            redAtt.addFlashAttribute("action", true);
            redAtt.addFlashAttribute("added", userRegistrationDto.getNom() + " " + userRegistrationDto.getPrenom());
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "Admin/User/users";
        }
    }

    @PatchMapping(path = "/{id}")
    public String editUser(@PathVariable("id") String id, @ModelAttribute EditUserDTO incomingUser, Model model, RedirectAttributes redirectAttributes) {
        UUID convertedId = UUID.fromString(String.valueOf(id));
        User user = userRepository.findById(convertedId).orElse(null);

        if (user == null)
            return "redirect:/auth/login";

        if (!userService.hasUniqueAttributes(user, incomingUser, redirectAttributes))
            return "redirect:/profile";

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
            return "redirect:/auth/login";

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
    }@PutMapping("/{id}")
    @Transactional
    public String updateUser(
            @PathVariable UUID id,
            EditUserDTO editUserDTO,
            RedirectAttributes redirectAttributes) {

        try {
            System.out.println(editUserDTO);

            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/users";
            }

            User user = userOpt.get();

            if (!userService.hasUniqueAttributes(user, editUserDTO, redirectAttributes)) {
                redirectAttributes.addFlashAttribute("actionAttributesExists", true);
                redirectAttributes.addFlashAttribute("emailerror", editUserDTO.getEmail());
                return "redirect:/users";
            }

            if (editUserDTO.getGenre() != null) {
                try {
                    user.setGenre(Genre.valueOf(editUserDTO.getGenre().name()));
                } catch (IllegalArgumentException e) {
                    redirectAttributes.addFlashAttribute("error", "Invalid genre value: " + editUserDTO.getGenre());
                    return "redirect:/users";
                }
            }

            if (editUserDTO.getRole() != null) {
                try {
                    RoleEnum roleEnum = editUserDTO.getRole();
                    Role role = roleRepository.findByRole(roleEnum);
                    if (role == null) {
                        redirectAttributes.addFlashAttribute("error", "Invalid role: " + roleEnum.name());
                        return "redirect:/users";
                    }
                    user.setRole(role);
                } catch (IllegalArgumentException e) {
                    redirectAttributes.addFlashAttribute("error", "Invalid role value: " + editUserDTO.getRole());
                    return "redirect:/users";
                }
            }

            editUserDTO.setUserDetails(user);

            userRepository.save(user);

            String successMessage = "User updated successfully: " + user.getNom() + " " + user.getPrenom();
            redirectAttributes.addFlashAttribute("success", successMessage);
            redirectAttributes.addFlashAttribute("actionAttributesExists", true);
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return "redirect:/users";
    }



}
