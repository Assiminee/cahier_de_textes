package upf.pjt.cahier_de_textes.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.services.AffectationService;
import upf.pjt.cahier_de_textes.services.UserService;

@Controller
@RequestMapping("/affectations")
public class AffectationController {
    private final int SIZE = 10;
    private final AffectationService affectationService;

    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @GetMapping
    public String getAffectations(
            Model model,
            @RequestParam(required = false, defaultValue = "") String filiere,
            @RequestParam(required = false, defaultValue = "") String module,
            @RequestParam(required = false, defaultValue = "") String professeur,
            @RequestParam(required = false, defaultValue = "") String jour,
            @RequestParam(required = false, defaultValue = "0") int heure,
            @RequestParam(defaultValue = "0") int page
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Pageable pageable = PageRequest.of(page, SIZE);
        Page<AffectationDTO> affectations = affectationService
                .getAffectationDTOPage(filiere, module, professeur, heure, jour, pageable);

        model.addAttribute("user", user);
        model.addAttribute("affs", affectations);
        model.addAttribute("filiere", filiere);
        model.addAttribute("module", module);
        model.addAttribute("professeur", professeur);
        model.addAttribute("jour", jour);
        model.addAttribute("heure", heure);

        return "affectations/index";
    }
}
