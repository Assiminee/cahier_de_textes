package upf.pjt.cahier_de_textes.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.FiliereDTO;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import upf.pjt.cahier_de_textes.dao.repositories.FiliereRepository;
import org.springframework.data.domain.Pageable;
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.services.FilieresService;
import upf.pjt.cahier_de_textes.services.UserService;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/filieres")
public class FiliereController {
    private final FiliereRepository filiereRepository;
    private final ProfesseurRepository professeurRepository;
    private final ModuleRepository moduleRepository;
    private final FilieresService filieresService;
    private final int SIZE = 10;

    public FiliereController(
            FiliereRepository filiereRepository,
            ProfesseurRepository professeurRepository,
            ModuleRepository moduleRepository,
            FilieresService filieresService
    ) {
        this.filiereRepository = filiereRepository;
        this.professeurRepository = professeurRepository;
        this.moduleRepository = moduleRepository;
        this.filieresService = filieresService;
    }

    @GetMapping
    public String filieres(
            Model model,
            @RequestParam(required = false, defaultValue = "") String intitule,
            @RequestParam(required = false, defaultValue = "") String coordinateur,
            @RequestParam(required = false, defaultValue = "") String diplome,
            @RequestParam(required = false, defaultValue = "true")  boolean reconnaissance,
            @RequestParam(defaultValue = "0") int page
    ) {
        User admin = UserService.getAuthenticatedAdmin();

        if (admin == null)
            return "redirect:/auth/login";

        Pageable pageable = PageRequest.of(page, SIZE, Sort.by("intitule").ascending());
        Diplome d = Diplome.getDiplome(diplome);
        Page<FiliereDTO> filieres = filieresService.mapFilieres(reconnaissance, intitule, coordinateur, d, pageable);
        Map<String, String> packagedParams = FilieresService.packageParams(intitule, coordinateur, diplome, reconnaissance);

        model.addAttribute("params", packagedParams);
        model.addAttribute("user", admin);
        model.addAttribute("filieres", filieres);
        model.addAttribute("diplomes", List.of(Diplome.values()));
        model.addAttribute("profs", professeurRepository.findAll());
        model.addAttribute("modules", moduleRepository.findAll());

        return "Admin/filiere/filiere";
    }

    @GetMapping("/{id}")
    public String filiere() {
        return "test";
    }
}
