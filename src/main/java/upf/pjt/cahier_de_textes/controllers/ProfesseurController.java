package upf.pjt.cahier_de_textes.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.dtos.ProfEditDto;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Qualification;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/professeur")
public class ProfesseurController {


    private final ProfesseurRepository professeurRepository;
    private  final RoleRepository roleRepository ;
    private  final  UserRepository userRepository ;
    private  final QualificationRepository qualificationRepository ;
    private final ModuleRepository moduleRepository ;
    private final FiliereRepository filiereRepository ;

    @Autowired
    public ProfesseurController(ProfesseurRepository professeurRepository , RoleRepository roleRepository , UserRepository userRepository, QualificationRepository qualificationRepository, ModuleRepository moduleRepository, FiliereRepository filiereRepository) {
        this.professeurRepository = professeurRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.qualificationRepository = qualificationRepository;
        this.moduleRepository = moduleRepository;
        this.filiereRepository = filiereRepository;
    }

    @GetMapping("/qualifications")
    public String qualifications(HttpServletRequest request, HttpServletResponse response, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof CustomUserDetails cud) {
            UUID id = cud.getUser().getId();
            Professeur prof = professeurRepository.findById(id).orElse(null);

            if (prof != null) {
                model.addAttribute("user", prof);
                return "Professeur/qualifications";
            }
        }

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, auth);
        return "redirect:/auth/login";
    }
    @GetMapping()
    public String showProfessors(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String nomComplet,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String grade
    ) {
        // Authenticate and set model attributes
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser); // Add the user to the model

            model.addAttribute("grades", Grade.values());
            model.addAttribute("roles", RoleEnum.values());
            model.addAttribute("genres", Genre.values());
        }

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

        // 1) Load security context (if needed)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser);
            model.addAttribute("grades", Grade.values());
            model.addAttribute("roles", RoleEnum.values());
            model.addAttribute("genres", Genre.values());
        }

        // 2) Fetch existing professor
        Optional<Professeur> existingProfOpt = professeurRepository.findById(id);
        if (existingProfOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Professeur introuvable.");
            return "redirect:/professeur";
        }
        Professeur existingProf = existingProfOpt.get();

        // 3) Check if any other professor already uses this email, CIN, or telephone
        boolean alreadyExists = professeurRepository.existsByEmailOrCinOrTelephone(
                updatedProfDto.getEmail(),
                updatedProfDto.getCin(),
                updatedProfDto.getTelephone(),
                existingProf.getId() // exclude current prof by ID
        );

        if (alreadyExists) {
            // If so, redirect with an error message
            redirectAttributes.addFlashAttribute("error",
                    "Un professeur avec cet email, CIN ou téléphone existe déjà.");
            return "redirect:/professeur";
        }

        // 4) Everything is okay; update the fields
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        updatedProfDto.updateEntity(existingProf, encoder);

        // 5) Save
        professeurRepository.save(existingProf);

        // 6) Success message
        redirectAttributes.addFlashAttribute("success",
                "Professeur mis à jour avec succès.");

        return "redirect:/professeur";
    }


    @GetMapping("/viewprof")
    public String viewProf(Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser); // Add the user to the model
            model.addAttribute("grades", Grade.values());
            model.addAttribute("roles", RoleEnum.values());
            model.addAttribute("genres", Genre.values());
        }
        return "Admin/Professeur/profView";
    }


    @DeleteMapping("/{id}")
    @Transactional
    public String deleteProf(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        Optional<Professeur> professeurOpt = professeurRepository.findById(id);

        if (professeurOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("actionAttributesExists", true);
            redirectAttributes.addFlashAttribute("error", "Professeur introuvable.");
            return "redirect:/professeur";
        }

        Professeur professeur = professeurOpt.get();

        for (Module module : professeur.getModules()) {
            module.setResponsable(null);
            moduleRepository.save(module);
        }

        if (professeur.getFiliere() != null) {
            professeur.getFiliere().setCoordinateur(null);
            filiereRepository.save(professeur.getFiliere());
        }

        professeurRepository.delete(professeur);

        redirectAttributes.addFlashAttribute("action", true);
        redirectAttributes.addFlashAttribute("deleted", professeur.getNom() + " " + professeur.getPrenom());

        return "redirect:/professeur";
    }

 @GetMapping("/{id}")
    public String getProfesseurById(
            @PathVariable UUID id,
            Model model,
            @RequestParam(name="view", defaultValue="false") boolean view,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Professeur> professeurOpt = professeurRepository.findById(id);

        if (professeurOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Professeur introuvable.");
            return "redirect:/professeur";
        }

        Professeur professeur = professeurOpt.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User currentUser = userDetails.getUser();
            model.addAttribute("user", currentUser); // Add the user to the model
        }

        model.addAttribute("professeur", professeur);
        model.addAttribute("grades", Grade.values());
        model.addAttribute("roles", RoleEnum.values());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("qualifications", professeur.getQualifications());
        model.addAttribute("view", view);

        return "Admin/Professeur/profView"; // Path to the Thymeleaf template
    }


    @PostMapping()
    public String addProfesseur(
            @Valid Professeur professeur,
            RedirectAttributes redirectAttributes
    ) {
        if (professeurRepository.existsByEmailOrCinOrTelephone(
                professeur.getEmail(),
                professeur.getCin(),
                professeur.getTelephone(),
                null)) {
            redirectAttributes.addFlashAttribute("actionAttributesExists", true);
            redirectAttributes.addFlashAttribute("error", "A professor with this email, CIN, or telephone already exists.");
            return "redirect:/professeur";
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

            professeurRepository.save(professeur);

            redirectAttributes.addFlashAttribute("action", true);
            redirectAttributes.addFlashAttribute("added", professeur.getNom() + " " + professeur.getPrenom());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while adding the professor: " + e.getMessage());
        }

        return "redirect:/professeur";
    }


}
