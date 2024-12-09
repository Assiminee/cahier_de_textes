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

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(name = "User management endpoints", path = "/users")
public class UserController {
    private final ProfileController profileController;
    private UserRepository userRepository;
    private ProfesseurRepository professeurRepository;

    @Autowired
    public UserController(UserRepository userRepository, ProfesseurRepository professeurRepository, ProfileController profileController) {
        this.userRepository = userRepository;
        this.professeurRepository = professeurRepository;
        this.profileController = profileController;
    }

    @PostMapping(path = "/{id}")
    public String editUser(@PathVariable("id") String id, @ModelAttribute EditUserDTO incomingUser, Model model, RedirectAttributes redirectAttributes) {
        if (incomingUser == null)
            return "redirect:/profile";

        UUID convertedId = UUID.fromString(String.valueOf(id));
        User user = userRepository.findById(convertedId).orElse(null);

        if (user == null)
            return "profile/profile";

        boolean emailExists = false, telephoneExists = false, cinExists = false;

        if (!user.getEmail().equals(incomingUser.getEmail()))
            emailExists = userRepository.existsByEmail(incomingUser.getEmail());

        if (!user.getTelephone().equals(incomingUser.getTelephone()))
            telephoneExists = userRepository.existsByTelephone(incomingUser.getTelephone());

        if (!user.getCin().equals(incomingUser.getCin()))
            cinExists = userRepository.existsByCin(incomingUser.getCin());

        if (emailExists || telephoneExists || cinExists) {
            redirectAttributes.addFlashAttribute("error", true);
            if (emailExists) {
                redirectAttributes.addFlashAttribute("email",
                        "L'email '" + incomingUser.getEmail() + "' existe deja");
            }
            if (telephoneExists) {
                redirectAttributes.addFlashAttribute("telephone",
                        "Le numero de telephone '" + incomingUser.getTelephone() + "' existe deja");
            }
            if (cinExists) {
                redirectAttributes.addFlashAttribute("cin",
                        "Le CIN '" + incomingUser.getCin() + "' existe deja");
            }
            profileController.profile(model);
            return "redirect:/profile";
        }

        if (incomingUser.getRole().name().equals("ROLE_PROF")) {
            Optional<Professeur> prof = professeurRepository.findById(convertedId);

            if (prof.isEmpty())
                return "redirect:/profile";

            Professeur professeur = prof.get();
            log.info("Old prof data:\n{}", professeur);
            incomingUser.setUserDetails(professeur);
            professeurRepository.save(professeur);
            log.info("Updated prof:\n{}", professeur);
            model.addAttribute("user", professeur);
            return "profile/profile";
        }

        Optional<User> optionalUser = userRepository.findById(convertedId);

        if (optionalUser.isEmpty())
            return "redirect:/profile";

        User loggedUser = optionalUser.get();
        log.info("Old user data:\n{}", loggedUser);
        incomingUser.setUserDetails(loggedUser);
        userRepository.save(loggedUser);
        log.info("Updated user:\n{}", incomingUser);
        model.addAttribute("user", loggedUser);

        return "profile/profile";
    }
}
