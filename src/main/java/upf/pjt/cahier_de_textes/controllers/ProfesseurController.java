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

import java.util.ArrayList;
import java.util.List;
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
    private final UserRepository userRepository;

    public ProfesseurController(
            ProfesseurRepository professeurRepository,
            RoleRepository roleRepository,
            ModuleRepository moduleRepository,
            FiliereRepository filiereRepository, AffectationRepository affectationRepository, CahierRepository cahierRepository, AffectationService affectationService,
            UserRepository userRepository) {
        this.professeurRepository = professeurRepository;
        this.roleRepository = roleRepository;
        this.moduleRepository = moduleRepository;
        this.filiereRepository = filiereRepository;
        this.affectationRepository = affectationRepository;
        this.cahierRepository = cahierRepository;
        this.affectationService = affectationService;
        this.userRepository = userRepository;
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
                nomComplet,
                email,
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
        if (user == null) return "redirect:/error/401";

        model.addAttribute("user", user);
        model.addAttribute("grades", Grade.values());
        model.addAttribute("roles", RoleEnum.values());
        model.addAttribute("genres", Genre.values());

        Professeur prof = professeurRepository.findById(id).orElse(null);

        if (prof == null) return "redirect:/error/404";

        boolean emailExists = userRepository.existsByIdIsNotAndEmailIgnoreCase(id, updatedProfDto.getEmail());
        boolean cinExists = userRepository.existsByIdIsNotAndCinIgnoreCase(id, updatedProfDto.getCin());
        boolean telephoneExists = userRepository.existsByIdIsNotAndTelephone(id, updatedProfDto.getTelephone());

        if (emailExists)
            redirectAttributes.addFlashAttribute("emailerror", updatedProfDto.getEmail());
        if (cinExists)
            redirectAttributes.addFlashAttribute("cin", updatedProfDto.getCin());
        if (telephoneExists)
            redirectAttributes.addFlashAttribute("telephone", updatedProfDto.getTelephone());

        if (emailExists || cinExists || telephoneExists) {
            redirectAttributes.addFlashAttribute("actionAttributesExists", true);
            return "redirect:/professeurs";
        }

        // 4) Update and save the professor...
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        updatedProfDto.updateEntity(prof, encoder);
        professeurRepository.save(prof);

        redirectAttributes.addFlashAttribute("actionEdit", true);
        redirectAttributes.addFlashAttribute("successEdit",
                " Le Professeur " + updatedProfDto.getNom() + ' ' + updatedProfDto.getPrenom() + " mis à jour avec succès.");
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
        model.addAttribute("add", true);

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
            @RequestParam(name = "view", defaultValue = "false") boolean view
    ) {
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
    ) {
        boolean emailExists = userRepository.existsByEmailIgnoreCase(professeur.getEmail());
        boolean cinExists = userRepository.existsByCinIgnoreCase(professeur.getCin());
        boolean telephoneExists = userRepository.existsByTelephone(professeur.getTelephone());

        if (emailExists)
            redirectAttributes.addFlashAttribute("emailerror", professeur.getEmail());
        if (cinExists)
            redirectAttributes.addFlashAttribute("cin", professeur.getCin());
        if (telephoneExists)
            redirectAttributes.addFlashAttribute("telephone", professeur.getTelephone());

        if (emailExists || cinExists || telephoneExists) {
            redirectAttributes.addFlashAttribute("actionAttributesExists", true);
            return "redirect:/professeurs";
        }

        try {

            Role role = roleRepository.findByRole(RoleEnum.ROLE_PROF);
            professeur.setRole(role);

            if (professeur.getQualifications() != null) {
                List<Qualification> qualifications = new ArrayList<>(professeur.getQualifications());
                professeur.getQualifications().clear();
                for (Qualification qualification : qualifications) {
                    if (qualification.getIntitule() != null && qualification.getDateObtention() != null) {
                        qualification.setProf(professeur);
                        professeur.getQualifications().add(qualification);
                    }
                }
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            professeur.setPwd(encoder.encode(professeur.getPwd()));

            professeur = professeurRepository.save(professeur);

            redirectAttributes.addFlashAttribute("action", true);
            redirectAttributes.addFlashAttribute("added", professeur.getFullName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de l'ajout du professeur : " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("professeur", professeur);
        redirectAttributes.addFlashAttribute("view", false);
        return "redirect:/professeurs";
    }

}
