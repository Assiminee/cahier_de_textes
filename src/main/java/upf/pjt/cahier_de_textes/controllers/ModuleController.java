package upf.pjt.cahier_de_textes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(name = "Module management endpoints", path = "/modules")
public class ModuleController {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ProfesseurRepository professorRepository;

    @GetMapping()
    public String showModules(@RequestParam(required = false) String intitule, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser);
        }

        List<Module> modules;
        if (intitule == null || intitule.isBlank()) {
            modules = moduleRepository.findAll();
        } else {
            modules = moduleRepository.findByIntituleContainingIgnoreCase(intitule);
        }
        model.addAttribute("modules", modules);
        model.addAttribute("professors", professorRepository.findAll());
        model.addAttribute("searchTerm", intitule);
        return "Admin/module";
    }

    @PostMapping()
    public String addModule(@ModelAttribute Module module, Model model, RedirectAttributes redAtt) {
        try {
            // Fetch the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                User currentUser = userDetails.getUser();
                model.addAttribute("user", currentUser);
            }
            moduleRepository.save(module);
            System.out.println("Module: " + module);
            redAtt.addFlashAttribute("addSucessModule", "Module\t" + module.getIntitule() + "\ta été ajouté avec succès");
            return "redirect:/modules";
        } catch (IllegalArgumentException e) {
            redAtt.addFlashAttribute("errorAddModule", "Erreur : " + e.getMessage());
            return "Admin/module";
        }
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
    @GetMapping("/edit/{id}")
    public String showEditModule(@PathVariable UUID id, Model model) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));

        List<Professeur> professors = professorRepository.findAll();

        model.addAttribute("module", module);
        model.addAttribute("professors", professors);
        return "Admin/module";
    }
    @PostMapping("/edit/{id}")
    public String editModule(
            @PathVariable UUID id,
            @RequestParam String intitule,
            @RequestParam UUID responsable,
            @RequestParam Integer nombre_heures,
            @RequestParam String modeEvaluation,
            RedirectAttributes redirectAttributes) {
        try {
            Module existingModule = moduleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Module not found"));

            existingModule.setIntitule(intitule);
            existingModule.setResponsable(professorRepository.findById(responsable)
                    .orElseThrow(() -> new IllegalArgumentException("Responsable not found")));
            existingModule.setNombre_heures(nombre_heures);
            existingModule.setModeEvaluation(ModeEval.valueOf(modeEvaluation));

            moduleRepository.save(existingModule);

            redirectAttributes.addFlashAttribute("editSuccess", "Module updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("editError", "Error updating module: " + e.getMessage());
        }
        return "redirect:/modules";
    }
}

