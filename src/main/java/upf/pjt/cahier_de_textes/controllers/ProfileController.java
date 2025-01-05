package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.repositories.QualificationRepository;

@Controller
public class ProfileController {

    private final QualificationRepository qualificationRepository;
    private final ProfesseurRepository professeurRepository;

    @Autowired
    public ProfileController(ProfesseurRepository professeurRepository, QualificationRepository qualificationRepository) {
        this.professeurRepository = professeurRepository;
        this.qualificationRepository = qualificationRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User loggedUser = userDetails.getUser();
            UserDTO user = new UserDTO();

            user.setUserDTODetails(loggedUser);

            if (loggedUser.getRole().getAuthority().equals("ROLE_PROF"))
                user.setProfessorDetails(qualificationRepository, professeurRepository);

            model.addAttribute("user", user);
        }

        return "profile/profile";
    }
}
