package upf.pjt.cahier_de_textes.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardRestController  {
    private final UserRepository userRepository;
    private final ProfesseurRepository professeurRepository;

    public DashboardRestController (UserRepository userRepository, ProfesseurRepository professeurRepository) {
        this.userRepository = userRepository;
        this.professeurRepository = professeurRepository;
    }

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

