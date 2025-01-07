package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.entities.*;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.repositories.AffectationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.CahierRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.ModeEval;
import upf.pjt.cahier_de_textes.services.ModuleService;
import upf.pjt.cahier_de_textes.utils.AuthUtils;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(name = "Module management endpoints", path = "/modules")
public class ModuleController {

    private final ModuleRepository moduleRepository;
    private final  ProfesseurRepository professorRepository;
    private final CahierRepository cahierRepository;
    private final AffectationRepository affectationRepository;

    @Autowired
    public ModuleController(
            ModuleRepository moduleRepository,
            ProfesseurRepository professorRepository,
            CahierRepository cahierRepository, AffectationRepository affectationRepository) {
        this.moduleRepository = moduleRepository;
        this.professorRepository = professorRepository;
        this.cahierRepository = cahierRepository;
        this.affectationRepository = affectationRepository;
    }

    @GetMapping()
    public String showModules(
            @RequestParam(required = false, defaultValue = "") String intitule,
            @RequestParam(required = false, defaultValue = "") String responsable,
            @RequestParam(required = false, defaultValue = "") String modeEvaluation,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    )
    {
        try {
            User currentUser = AuthUtils.getAuthenticatedUser();
            model.addAttribute("user", currentUser);
        } catch (IllegalStateException e) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("intitule").ascending());

        ModeEval modeEvalEnum = null;
        if (modeEvaluation != null && !modeEvaluation.isBlank()) {
            try {
                modeEvalEnum = ModeEval.valueOf(modeEvaluation.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Invalid mode d'évaluation");
                return "Admin/Module/module";
            }
        }

        Page<Module> modulesPage = moduleRepository.filterModules(intitule, responsable, modeEvalEnum, min, max, pageable);

        List<Professeur> professors = professorRepository.findAll();

        model.addAttribute("professors", professors);
        model.addAttribute("modules", modulesPage.getContent());
        model.addAttribute("totalPages", modulesPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("searchTerm", intitule);
        model.addAttribute("responsableSearchTerm", responsable);
        model.addAttribute("modeEvaluation", modeEvaluation);
        model.addAttribute("min", min);
        model.addAttribute("max", max);

        return "Admin/Module/module";
    }


    @PostMapping()
    public String addModule(@ModelAttribute Module module, Model model, RedirectAttributes redAtt) {
        try {
            try {
                User currentUser = AuthUtils.getAuthenticatedUser();
                model.addAttribute("user", currentUser);
            } catch (IllegalStateException e) {
                return "redirect:/login";
            }

            if (moduleRepository.existsByIntituleIgnoreCase(module.getIntitule())) {
                redAtt.addFlashAttribute("errorAddModule", "Erreur : Un module avec cet intitulé existe déjà.");
                return "redirect:/modules";
            }
            if ( module.getNombre_heures() < 1 || module.getNombre_heures() > 48) {
                redAtt.addFlashAttribute("errorAddModule", "Erreur : Le nombre d'heures doit être compris entre 1 et 48.");
                return "redirect:/modules";
            }

            moduleRepository.save(module);
            redAtt.addFlashAttribute("addSucessModule", module.getIntitule());
        } catch (IllegalArgumentException e) {
            redAtt.addFlashAttribute("errorAddModule", "Erreur : " + e.getMessage());
        }
        return "redirect:/modules";
    }



    @DeleteMapping("/{id}")
    public String deleteModules(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            Module module = moduleRepository.findById(id).orElse(null);

            if (module == null)
                return "redirect:/error/404";

            for (Affectation aff : module.getAffectations()) {
                Cahier cahier = aff.getCahier();
                aff.setCahier(null);

                if (cahier != null) {
                    if (cahier.getEntrees() != null && !cahier.getEntrees().isEmpty()) {
                        cahier.setAffectation(null);
                        cahier.setArchived(true);

                        cahierRepository.save(cahier);
                    } else {
                        cahierRepository.delete(cahier);
                    }
                }

                affectationRepository.deleteById(aff.getId());
            }

            moduleRepository.delete(module);

            redirectAttributes.addFlashAttribute("action", true);
            redirectAttributes.addFlashAttribute("deleteSuccess",  module.getIntitule() );
        } catch (Exception e) {
            System.err.println("Error deleting module: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du module : " + e.getMessage());
        }
        return "redirect:/modules";
    }

    @GetMapping("/{id}")
    public String showEditModule(@PathVariable UUID id, Model model) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));

        List<Professeur> professors = professorRepository.findAll();

        model.addAttribute("module", module);
        model.addAttribute("professors", professors);
        return "Admin/Module/module";
    }

    @PutMapping("/{id}")
    public String editModule(
            @PathVariable UUID id,
            @RequestParam String intitule,
            @RequestParam(required = false) UUID responsable, // Optional parameter
            @RequestParam Integer nombre_heures,
            @RequestParam ModeEval modeEvaluation,
            RedirectAttributes redirectAttributes) {
        try {
            Module existingModule = moduleRepository.findById(id).orElse(null);

            if (existingModule == null)
                return "redirect:/error/404";

            existingModule.setIntitule(intitule);
            existingModule.setNombre_heures(nombre_heures);
            existingModule.setModeEvaluation(modeEvaluation);

            if (responsable != null) {
                existingModule.setResponsable(professorRepository.findById(responsable)
                        .orElseThrow(() -> new IllegalArgumentException("Responsable not found")));
            }

            for (Affectation aff : existingModule.getAffectations()) {
                Cahier cahier = aff.getCahier();

                if (cahier != null)
                    cahier.setModule(existingModule.getIntitule());
            }

            moduleRepository.save(existingModule);
            redirectAttributes.addFlashAttribute("editSucessModule", existingModule.getIntitule());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroreditModule", "Erreur Modification du Module , veuillez essayer plut tard  " + e.getMessage());
        }
        return "redirect:/modules";
    }

}

