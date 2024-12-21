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
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.ModeEval;
import upf.pjt.cahier_de_textes.utils.AuthUtils;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping(name = "Module management endpoints", path = "/modules")
public class ModuleController {

    private final ModuleRepository moduleRepository;
    private final  ProfesseurRepository professorRepository;

    @Autowired
    public ModuleController(
            ModuleRepository moduleRepository,
            ProfesseurRepository professorRepository
    ) {
        this.moduleRepository = moduleRepository;
        this.professorRepository = professorRepository;
    }
    @GetMapping()
    public String showModules(@RequestParam(required = false) String intitule,
                              @RequestParam(required = false) String responsable,
                              @RequestParam(required = false) ModeEval modeEvaluation,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        try {
            User currentUser = AuthUtils.getAuthenticatedUser();
            model.addAttribute("user", currentUser);
        } catch (IllegalStateException e) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("intitule").ascending());
        Page<Module> modulesPage = Page.empty(); // Initialize with a default value

        if ((intitule == null || intitule.isBlank()) &&
                (responsable == null || responsable.isBlank()) &&
                (modeEvaluation == null )) {
            modulesPage = moduleRepository.findAll(pageable);
        } else if (intitule != null && !intitule.isBlank() &&
                (responsable == null || responsable.isBlank()) &&
                (modeEvaluation == null )) {
            modulesPage = moduleRepository.findByIntituleContainingIgnoreCase(intitule, pageable);
        } else if ((intitule == null || intitule.isBlank()) &&
                responsable != null && !responsable.isBlank() &&
                (modeEvaluation == null )) {
            modulesPage = moduleRepository.findByResponsable_NomContainingIgnoreCase(responsable, pageable);
        } else if ((intitule == null || intitule.isBlank()) &&
                (responsable == null || responsable.isBlank()) &&
                modeEvaluation != null ) {
            modulesPage = moduleRepository.findByModeEvaluationContainingIgnoreCase(modeEvaluation, pageable);
        } else {
            modulesPage = moduleRepository.findByIntituleContainingIgnoreCaseAndResponsable_NomContainingIgnoreCaseAndModeEvaluation(
                    intitule, responsable, modeEvaluation, pageable);
        }

        model.addAttribute("modules", modulesPage.getContent());
        model.addAttribute("totalPages", modulesPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("searchTerm", intitule);
        model.addAttribute("responsableSearchTerm", responsable);
        model.addAttribute("modeEvaluation", modeEvaluation);
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

            moduleRepository.save(module);
            System.out.println("Module: " + module);
            redAtt.addFlashAttribute("addSucessModule", "Module\t" + module.getIntitule() + "\ta été ajouté avec succès");

        } catch (IllegalArgumentException e) {
            redAtt.addFlashAttribute("errorAddModule", "Erreur : " + e.getMessage());
        }
        return "redirect:/modules";
    }

    @DeleteMapping("/{id}")
    public String deleteModules(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Delete request received for module ID: " + id);

            Module module = moduleRepository.findById(id).orElse(null);
            if (module == null) {
                redirectAttributes.addFlashAttribute("error", "Module introuvable.");
                return "redirect:/modules";
            }
            moduleRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("action", true);
            redirectAttributes.addFlashAttribute("deleteSuccess", "Le module '" + module.getIntitule() + "' a été supprimé avec succès.");
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
            Module existingModule = moduleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Module not found"));

            existingModule.setIntitule(intitule);
            existingModule.setNombre_heures(nombre_heures);
            existingModule.setModeEvaluation(modeEvaluation);
            if (responsable != null) {
                existingModule.setResponsable(professorRepository.findById(responsable)
                        .orElseThrow(() -> new IllegalArgumentException("Responsable not found")));
            }

            moduleRepository.save(existingModule);
            redirectAttributes.addFlashAttribute("editSucessModule", "Module'" + existingModule.getIntitule()+ "' Modifier avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroreditModule", "Erreur Modification du Module , veuillez essayer plut tard  " + e.getMessage());
        }
        return "redirect:/modules";
    }

}

