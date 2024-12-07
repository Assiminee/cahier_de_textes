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

    @Controller
    public class ProfileController {

        private final ProfesseurRepository professeurRepository;

        @Autowired
        public ProfileController(ProfesseurRepository professeurRepository) {
            this.professeurRepository = professeurRepository;
        }

        @GetMapping("/profile")
        public String profile(Model model) {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                User user = userDetails.getUser();

                // Add user to the model
                if (user.getRole().getAuthority().equals(RoleEnum.ROLE_PROF.name())) {
                    Professeur prof = professeurRepository.findById(user.getId()).orElse(null);
                    if (prof != null) {
                        model.addAttribute("user", prof);
                    } else {
                        throw new IllegalStateException("Professeur data not found for user ID: " + user.getId());
                    }
                } else {
                    model.addAttribute("user", user);
                }
            } else {
                throw new IllegalStateException("Authentication principal is not a valid user.");
            }

            return "profile/profile";
        }
    }

