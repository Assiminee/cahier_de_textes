package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.AffectationDTO;
import upf.pjt.cahier_de_textes.dao.entities.*;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.services.AffectationService;
import upf.pjt.cahier_de_textes.services.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.dtos.ProfEditDto;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/professeurs")
public class ProfesseurController {
    private final ProfesseurRepository professeurRepository;
    private final RoleRepository roleRepository;
    private final ModuleRepository moduleRepository;
    private final FiliereRepository filiereRepository;
    private final AffectationRepository affectationRepository;
    private final CahierRepository cahierRepository;
    private final AffectationService affectationService;
    private final int SIZE = 10;

    public ProfesseurController(
            ProfesseurRepository professeurRepository,
            RoleRepository roleRepository,
            ModuleRepository moduleRepository,
            FiliereRepository filiereRepository, AffectationRepository affectationRepository, CahierRepository cahierRepository, AffectationService affectationService
    ) {
        this.professeurRepository = professeurRepository;
        this.roleRepository = roleRepository;
        this.moduleRepository = moduleRepository;
        this.filiereRepository = filiereRepository;
        this.affectationRepository = affectationRepository;
        this.cahierRepository = cahierRepository;
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

    @GetMapping
    public String showProfessors(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String nomComplet,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String grade
    ) {
        // Authenticate and set model attributes
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        model.addAttribute("user", user);
        model.addAttribute("grades", Grade.values());
        model.addAttribute("roles", RoleEnum.values());
        model.addAttribute("genres", Genre.values());

        // Parse grade if provided
        Grade parsedGrade = grade.isBlank() ? null : Grade.valueOf(grade.toUpperCase());

        // Setup pagination and sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());

        // Fetch filtered professors
        Page<Professeur> professorsPage = professeurRepository.findFilteredProfessors(
                nomComplet.isBlank() ? null : nomComplet,
                email.isBlank() ? null : email,
                parsedGrade,
                pageable
        );

        // Add results to the model
        model.addAttribute("professors", professorsPage.getContent());
        model.addAttribute("totalPages", professorsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("nomComplet", nomComplet);
        model.addAttribute("email", email);
        model.addAttribute("grade", grade);

        return "Admin/Professeur/professeur";
    }

    @PutMapping("/{id}")
    @Transactional
    public String editProf(@PathVariable UUID id,
                           Model model,
                           @Valid ProfEditDto updatedProfDto,
                           RedirectAttributes redirectAttributes) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        // 1) Load security context (if needed)

        model.addAttribute("user", user);
        model.addAttribute("grades", Grade.values());
        model.addAttribute("roles", RoleEnum.values());
        model.addAttribute("genres", Genre.values());

        // 2) Fetch existing professor
        Professeur prof = professeurRepository.findById(id).orElse(null);

        if (prof == null)
            return "redirect:/error/404";

        // 3) Check if any other professor already uses this email, CIN, or telephone
        boolean alreadyExists = professeurRepository.existsByEmailOrCinOrTelephone(
                updatedProfDto.getEmail(),
                updatedProfDto.getCin(),
                updatedProfDto.getTelephone(),
                prof.getId() // exclude current prof by ID
        );

        if (alreadyExists) {
            // If so, redirect with an error message
            redirectAttributes.addFlashAttribute("error",
                    "Un professeur avec cet email, CIN ou téléphone existe déjà.");
            return "redirect:/professeurs";
        }

        // 4) Everything is okay; update the fields
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        updatedProfDto.updateEntity(prof, encoder);

        // 5) Save
        professeurRepository.save(prof);

        // 6) Success message
        redirectAttributes.addFlashAttribute("success",
                "Professeur mis à jour avec succès.");

        return "redirect:/professeurs";
    }


    @GetMapping("/viewprof")
    public String viewProf(Model model
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        model.addAttribute("user", user); // Add the user to the model
        model.addAttribute("grades", Grade.values());
        model.addAttribute("roles", RoleEnum.values());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("view", false);

        return "Admin/Professeur/profView";
    }


    @DeleteMapping("/{id}")
    @Transactional
    public String deleteProf(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        Professeur prof = professeurRepository.findById(id).orElse(null);

        if (prof == null)
            return "redirect:/error/404";

        for (Module module : prof.getModules()) {
            module.setResponsable(null);
            moduleRepository.save(module);
        }

        if (prof.getFiliere() != null) {
            prof.getFiliere().setCoordinateur(null);
            filiereRepository.save(prof.getFiliere());
        }

        for (Affectation aff : prof.getAffectations()) {
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

        professeurRepository.delete(prof);

        redirectAttributes.addFlashAttribute("action", true);
        redirectAttributes.addFlashAttribute("deleted", prof.getFullName());

        return "redirect:/professeurs";
    }

    @GetMapping("/{id}")
    public String getProfesseurById(
            @PathVariable UUID id,
            Model model,
            @RequestParam(name = "view", defaultValue = "false") boolean view,
            RedirectAttributes redirectAttributes
    )
    {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Professeur prof = professeurRepository.findById(id).orElse(null);

        if (prof == null)
            return "redirect:/error/404";

        model.addAttribute("user", user);
        model.addAttribute("professeur", prof);
        model.addAttribute("grades", Grade.values());
        model.addAttribute("roles", RoleEnum.values());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("qualifications", prof.getQualifications());
        model.addAttribute("view", view);

        return "Admin/Professeur/profView"; // Path to the Thymeleaf template
    }


    @PostMapping()
    public String addProfesseur(
            @Valid Professeur professeur,
            RedirectAttributes redirectAttributes
    )
    {
        boolean exists = professeurRepository.existsByEmailOrCinOrTelephone(
                            professeur.getEmail(),
                            professeur.getCin(),
                            professeur.getTelephone(),
                            null
                        );
        System.out.println(exists);
        if (exists) {
            redirectAttributes.addFlashAttribute("actionAttributesExists", true);
            redirectAttributes.addFlashAttribute("error", "L'email, CIN, ou telephone existe déja.");
            redirectAttributes.addFlashAttribute("professeur", professeur);
            redirectAttributes.addFlashAttribute("view", false);
            return "redirect:/professeurs/viewprof";
        }

        try {
            Role role = roleRepository.findByRole(RoleEnum.ROLE_PROF);
            professeur.setRole(role);

            if (professeur.getQualifications() != null) {
                for (Qualification qualification : professeur.getQualifications()) {
                    qualification.setProf(professeur); // Associate qualifications with the professor
                }
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            professeur.setPwd(encoder.encode(professeur.getPwd()));

            professeur = professeurRepository.save(professeur);

            redirectAttributes.addFlashAttribute("action", true);
            redirectAttributes.addFlashAttribute("added", professeur.getFullName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "An error occurred while adding the professor: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("professeur", professeur);
        redirectAttributes.addFlashAttribute("view", false);
        return "redirect:/professeurs/viewprof";
    }
}
