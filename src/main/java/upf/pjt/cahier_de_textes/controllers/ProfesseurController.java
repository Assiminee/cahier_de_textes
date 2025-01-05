package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.services.AffectationService;
import upf.pjt.cahier_de_textes.services.UserService;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/professeurs")
public class ProfesseurController {
    private final int SIZE = 10;
    private final AffectationService affectationService;

    public ProfesseurController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @GetMapping("/{id}/affectations")
    public String getProfesseurAffectations(
            @PathVariable("id") UUID id,
            Model model,
            @RequestParam(required = false, defaultValue = "") String filiere,
            @RequestParam(required = false, defaultValue = "") String module,
            @RequestParam(required = false, defaultValue = "") String jour,
            @RequestParam(required = false, defaultValue = "0") int heure,
            @RequestParam(defaultValue = "0") int page
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        if (!user.getId().equals(id))
            return "redirect:/error/403";

        Pageable pageable = PageRequest.of(page, SIZE);
        Page<AffectationDTO> affectations = affectationService
                .getProfesseurAffectationDTOPage(id, filiere, module, heure, jour, pageable);

        model.addAttribute("user", user);
        model.addAttribute("affs", affectations);
        model.addAttribute("filiere", filiere);
        model.addAttribute("module", module);
        model.addAttribute("jour", jour);
        model.addAttribute("heure", heure);

        return "affectations/index";
    }
}
