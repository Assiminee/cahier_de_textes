package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;

import java.util.*;

@Controller
public class ProfileController {

    private ProfesseurRepository professeurRepository;

    @Autowired
    public ProfileController(ProfesseurRepository professeurRepository) {
        this.professeurRepository = professeurRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model, RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User user = userDetails.getUser();

            if (Objects.equals(user.getRole().getAuthority(), RoleEnum.ROLE_PROF.name())) {
                UUID convertedId = UUID.fromString(String.valueOf(user.getId()));
                Optional<Professeur> prof = professeurRepository.findById(convertedId);

                if (prof.isEmpty()) {
                    redirectAttributes.addFlashAttribute("login_err", true);
                    return "redirect:/auth/login";
                }

                model.addAttribute("user", prof.get());
            }
            else {
                model.addAttribute("user", user);
            }
        }

        return "profile/profile";
    }
}
