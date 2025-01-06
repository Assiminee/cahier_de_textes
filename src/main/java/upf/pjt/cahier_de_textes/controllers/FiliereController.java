package upf.pjt.cahier_de_textes.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.FiliereDTO;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import upf.pjt.cahier_de_textes.dao.repositories.AffectationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.FiliereRepository;
import org.springframework.data.domain.Pageable;
import upf.pjt.cahier_de_textes.dao.repositories.ModuleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.services.AffectationService;
import upf.pjt.cahier_de_textes.services.FilieresService;
import upf.pjt.cahier_de_textes.services.UserService;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/filieres")
public class FiliereController {
    private final FiliereRepository filiereRepository;
    private final ProfesseurRepository professeurRepository;
    private final ModuleRepository moduleRepository;
    private final FilieresService filieresService;
    private final ObjectMapper objectMapper;
    private final AffectationRepository affectationRepository;
    private final int SIZE = 10;
    private final AffectationService affectationService;

    public FiliereController(
            FiliereRepository filiereRepository,
            ProfesseurRepository professeurRepository,
            ModuleRepository moduleRepository,
            FilieresService filieresService,
            ObjectMapper objectMapper,
            AffectationRepository affectationRepository, AffectationService affectationService) {
        this.filiereRepository = filiereRepository;
        this.professeurRepository = professeurRepository;
        this.moduleRepository = moduleRepository;
        this.filieresService = filieresService;
        this.objectMapper = objectMapper;
        this.affectationRepository = affectationRepository;
        this.affectationService = affectationService;
    }

    @GetMapping
    public String filieres(
            Model model,
            @RequestParam(required = false, defaultValue = "") String intitule,
            @RequestParam(required = false, defaultValue = "") String coordinateur,
            @RequestParam(required = false, defaultValue = "") String diplome,
            @RequestParam(required = false) Boolean reconnaissance,
            @RequestParam(defaultValue = "0") int page
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Pageable pageable = PageRequest.of(page, SIZE, Sort.by("intitule").ascending());
        Diplome d = Diplome.getDiplome(diplome);
        Page<FiliereDTO> filieres = filieresService.mapFilieres(reconnaissance, intitule, coordinateur, d, pageable);
        Map<String, String> packagedParams = FilieresService.packageParams(intitule, coordinateur, diplome, reconnaissance);

        model.addAttribute("params", packagedParams);
        model.addAttribute("user", user);
        model.addAttribute("filieres", filieres);
        model.addAttribute("diplomes", List.of(Diplome.values()));
        model.addAttribute("profs", professeurRepository.findAllByFiliereIsNull());
        model.addAttribute("modules", moduleRepository.findAll());

        return "Admin/filiere/filiere";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String saveFiliere(@ModelAttribute Filiere filiere, RedirectAttributes redAtts) {
        redAtts.addFlashAttribute("post", true);

        try {
            Boolean dupData = filieresService.duplicateData(filiere, "post", redAtts);

            if (!dupData) {
                filiere = filiereRepository.save(filiere);
                redAtts.addFlashAttribute("msg", filiere.getIntitule());
            }
        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));

            redAtts.addFlashAttribute("error", true);
            redAtts.addFlashAttribute("msg", "L'ajout de la filière a echouée");
        }

        return "redirect:/filieres";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Transactional
    public String deleteFiliere(@PathVariable("id") UUID id, RedirectAttributes redAtts) {
        redAtts.addFlashAttribute("delete", true);
        try {
            Filiere filiere = filiereRepository.findById(id).orElse(null);

            if (filiere == null) {
                redAtts.addFlashAttribute("error", true);
                redAtts.addFlashAttribute("msg", "La filière que vous avez tenté de supprimer n'existe pas");

                return "redirect:/filieres";
            }

            Professeur coordinateur = filiere.getCoordinateur();

            coordinateur.setFiliere(null);
            professeurRepository.save(coordinateur);

            filiere.setCoordinateur(null);
            deleteAffectationsAndCahiers(filiere);
            filiereRepository.delete(filiere);

            redAtts.addFlashAttribute("error", false);
            redAtts.addFlashAttribute("msg", filiere.getIntitule());
        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));

            redAtts.addFlashAttribute("error", true);
            redAtts.addFlashAttribute("msg", "La suppression de la filère a échouée");
        }
        return "redirect:/filieres";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Transactional
    public String modifyFiliere(@PathVariable("id") UUID id, @ModelAttribute Filiere filiere, RedirectAttributes redAtts) {
        redAtts.addFlashAttribute("put", true);
        try {
            Filiere fil = filiereRepository.findById(id).orElse(null);

            if (fil == null) {
                redAtts.addFlashAttribute("error", true);
                redAtts.addFlashAttribute("msg", "La filière que vous avez tenté de supprimer n'existe pas");

                return "redirect:/filieres";
            }

            Boolean dupData = filieresService.duplicateData(filiere, "put", redAtts);

            if (!dupData) {
                fil.setCoordinateur(filiere.getCoordinateur());
                fil.setDiplome(filiere.getDiplome());
                fil.setIntitule(filiere.getIntitule());
                fil.setDateExpiration(filiere.getDateExpiration());
                fil.setDateReconnaissance(filiere.getDateReconnaissance());
                fil.setNombreAnnees(filiere.getNombreAnnees());

                fil = filiereRepository.save(fil);
                redAtts.addFlashAttribute("msg", fil.getIntitule());
            }
        } catch (Exception e) {
            log.error(String.valueOf(e.getCause()));

            redAtts.addFlashAttribute("error", true);
            redAtts.addFlashAttribute("msg", "La modification de la filère a échouée");
        }

        return "redirect:/filieres";
    }

    @GetMapping("/{id}/affectations")
    public String getFiliereAffectations(
            @PathVariable("id") UUID id,
            Model model,
            RedirectAttributes redAtts
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Filiere filiere = filiereRepository.findById(id).orElse(null);

        if (filiere == null)
            return "redirect:/filieres";

        FiliereDTO filiereDTO = new FiliereDTO(filiere);

        try {
            filiereDTO.setAffectations(filiere, objectMapper);
        } catch (JsonProcessingException e) {
            redAtts.addFlashAttribute("error", true);
            redAtts.addFlashAttribute("msg", "Une erreure s'est produite lors de la recuperation des affectations. Veuillez reessayer plutard");
            return "redirect:/filieres";
        }

        model.addAttribute("user", user);
        model.addAttribute("filiere", filiereDTO);
        model.addAttribute("modules", moduleRepository.findAll());
        model.addAttribute("profs", professeurRepository.findAll());

        return "ss/affectations/index";
    }

    private void deleteAffectationsAndCahiers(Filiere filiere) {
        List<Affectation> affectations = affectationRepository.findAllByFiliere_Id(filiere.getId());

        for (Affectation affectation : affectations) {
            affectationService.deleteAffectation(affectation);
        }
    }
}
