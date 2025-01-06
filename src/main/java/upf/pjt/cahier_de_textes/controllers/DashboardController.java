package upf.pjt.cahier_de_textes.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;


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

@RestController
@RequestMapping("/api")
class DashboardApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfesseurRepository professeurRepository;

    // Method to return JSON data for the dashboard
    @GetMapping("/dashboard-stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.countAllUsers());
        stats.put("femaleUsers", userRepository.countUsersByGender(Genre.F));
        stats.put("maleUsers", userRepository.countUsersByGender(Genre.M));

        // Role distribution
        List<Object[]> roleCounts = userRepository.countUsersByRole();
        Map<String, Integer> roleData = new HashMap<>();
        for (Object[] roleCount : roleCounts) {
            roleData.put(roleCount[0].toString(), ((Number) roleCount[1]).intValue());
        }

        stats.put("roleData", roleData);

        List<Object[]> gradeCounts = professeurRepository.countProfessorsByGrade();
        Map<String, Integer> gradeData = new HashMap<>();
        for (Object[] gradeCount : gradeCounts) {
            gradeData.put(gradeCount[0].toString(), ((Number) gradeCount[1]).intValue());
        }
        stats.put("gradeData", gradeData);


        return stats;
    }
}
