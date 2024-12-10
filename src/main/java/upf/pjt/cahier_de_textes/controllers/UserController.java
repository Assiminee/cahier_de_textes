package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.dtos.EditUserDTO;
import upf.pjt.cahier_de_textes.services.UserService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(name = "User management endpoints", path = "/users")
public class UserController {
    private final ProfileController profileController;
    private final UserService userService;
    private UserRepository userRepository;
    private ProfesseurRepository professeurRepository;

    @Autowired
    public UserController(UserRepository userRepository, ProfesseurRepository professeurRepository, ProfileController profileController, UserService userService) {
        this.userRepository = userRepository;
        this.professeurRepository = professeurRepository;
        this.profileController = profileController;
        this.userService = userService;
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
}
