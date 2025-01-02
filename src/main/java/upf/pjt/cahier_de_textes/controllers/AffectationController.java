package upf.pjt.cahier_de_textes.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.repositories.AffectationRepository;
import upf.pjt.cahier_de_textes.services.AffectationService;
import upf.pjt.cahier_de_textes.services.UserService;

@Controller
@RequestMapping("/affectations")
public class AffectationController {
    private final int SIZE = 10;
    private final AffectationRepository affectationRepository;
    private final AffectationService affectationService;

    public AffectationController(AffectationRepository affectationRepository, AffectationService affectationService) {
        this.affectationRepository = affectationRepository;
        this.affectationService = affectationService;
    }

    @GetMapping
    public String getAffectations(
            Model model,
            @RequestParam(required = false, defaultValue = "") String filiere,
            @RequestParam(required = false, defaultValue = "") String module,
            @RequestParam(required = false, defaultValue = "") String professeur,
            @RequestParam(defaultValue = "0") int page
    ) {
        System.out.println(module);
        System.out.println(professeur);
        System.out.println(filiere);
        UserDTO user = UserService.getAuthenticatedUser(new String[] {"ROLE_SS"});

        if (user == null)
            return "redirect:/auth/login";

        Pageable pageable = PageRequest.of(page, SIZE);
        Page<AffectationDTO> affectations = affectationService
                .getAffectationDTOPage(filiere, module, professeur, pageable);

        model.addAttribute("user", user);
        model.addAttribute("affs", affectations);
        model.addAttribute("filiere", filiere);
        model.addAttribute("module", module);
        model.addAttribute("professeur", professeur);

        for (AffectationDTO aff : affectations) {
            System.out.println(aff.getModule().getIntitule());
            System.out.println(aff.getFiliereIntitule());
            System.out.println(aff.getProfesseur().getFullName());
        }

        return "Admin/affectations/list/all_affectations";
    }
}
