package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
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
import upf.pjt.cahier_de_textes.dao.dtos.*;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.QualificationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.RoleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import upf.pjt.cahier_de_textes.services.UserDetailsServiceImpl;
import upf.pjt.cahier_de_textes.services.UserRegistrationService;
import upf.pjt.cahier_de_textes.services.UserService;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(name = "User management endpoints", path = "/users")
public class UserController {
    private final UserService userService;
    private final QualificationRepository qualificationRepository;
    private final UserRepository userRepository;
    private final ProfesseurRepository professeurRepository;
    private final UserRegistrationService userRegistrationService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, QualificationRepository qualificationRepository, UserRepository userRepository, ProfesseurRepository professeurRepository, UserRegistrationService userRegistrationService, RoleRepository roleRepository) {
        this.userService = userService;
        this.qualificationRepository = qualificationRepository;
        this.userRepository = userRepository;
        this.professeurRepository = professeurRepository;
        this.userRegistrationService = userRegistrationService;
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

    @PutMapping("/{id}")
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

            if (!userService.hasUniqueAttributes(id, editUserDTO, redirectAttributes)) {
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
                    RoleEnum roleEnum = RoleEnum.valueOf(editUserDTO.getRole());
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
