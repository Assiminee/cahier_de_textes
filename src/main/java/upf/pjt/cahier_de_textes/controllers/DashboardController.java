package upf.pjt.cahier_de_textes.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.entities.User;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String getDashboardPage(Model model) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails)

    {
        User currentUser = userDetails.getUser();
        model.addAttribute("user", currentUser);
    }
        model.addAttribute("pageTitle", "Statistiques de l'application");
        return "Admin/dashboard/dash"; // Name of the Thymeleaf HTML file (without .html)
    }
}
