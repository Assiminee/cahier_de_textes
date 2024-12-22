package upf.pjt.cahier_de_textes.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/professeur")
public class ProfesseurController {
    private final ProfesseurRepository professeurRepository;

    public ProfesseurController(ProfesseurRepository professeurRepository) {
        this.professeurRepository = professeurRepository;
    }

    @GetMapping("/qualifications")
    public String qualifications(HttpServletRequest request, HttpServletResponse response, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof CustomUserDetails cud) {
            UUID id = cud.getUser().getId();
            Professeur prof = professeurRepository.findById(id).orElse(null);

            if (prof != null) {
                model.addAttribute("user", prof);
                return "Professeur/qualifications";
            }
        }

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, auth);
        return "redirect:/auth/login";
    }
}
