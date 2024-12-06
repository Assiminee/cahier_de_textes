package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import upf.pjt.cahier_de_textes.dao.ProfesseurRepository;
import upf.pjt.cahier_de_textes.entities.Professeur;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.models.CustomUserDetails;
import upf.pjt.cahier_de_textes.services.UserService;

import java.util.Objects;

@Controller
public class ProfileController {

    private ProfesseurRepository professeurRepository;

    @Autowired
    public ProfileController(ProfesseurRepository professeurRepository) {
        this.professeurRepository = professeurRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User user = userDetails.getUser();

            if (Objects.equals(user.getRole().getAuthority(), RoleEnum.ROLE_PROF.name())) {
                Professeur prof = professeurRepository.getReferenceById(user.getId());
                model.addAttribute("user", prof);
            }
            else {
                model.addAttribute("user", user);
            }
        }

        return "profile/profile";
    }
}
