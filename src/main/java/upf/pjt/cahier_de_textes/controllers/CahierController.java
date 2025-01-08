package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.entities.Cahier;
import upf.pjt.cahier_de_textes.dao.entities.Entree;
import upf.pjt.cahier_de_textes.dao.entities.Objectif;
import upf.pjt.cahier_de_textes.dao.repositories.CahierRepository;
import upf.pjt.cahier_de_textes.dao.repositories.EntreeRepository;
import upf.pjt.cahier_de_textes.services.EntreeService;
import upf.pjt.cahier_de_textes.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/cahiers")
public class CahierController {
    private final CahierRepository cahierRepository;
    private final int SIZE = 10;
    private final EntreeRepository entreeRepository;

    public CahierController(CahierRepository cahierRepository, EntreeRepository entreeRepository) {
        this.cahierRepository = cahierRepository;
        this.entreeRepository = entreeRepository;
    }

    @GetMapping("/archive")
    public String getArchive(
            Model model,
            @RequestParam(required = false, defaultValue = "") String filiere,
            @RequestParam(required = false, defaultValue = "") String module,
            @RequestParam(required = false, defaultValue = "") String professeur,
            @RequestParam(required = false) Integer niveau,
            @RequestParam(required = false) Integer semestre,
            @RequestParam(required = false) Integer annee,
            @RequestParam(defaultValue = "0") int page
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Pageable pageable = PageRequest.of(page, SIZE);
        Page<Cahier> cahiers = cahierRepository
                .findArchivedCahiersByFilters(filiere, module, professeur, niveau, semestre, annee, pageable);

        model.addAttribute("user", user);
        model.addAttribute("cahiers", cahiers);
        model.addAttribute("filiere", filiere);
        model.addAttribute("module", module);
        model.addAttribute("professeur", professeur);
        model.addAttribute("niveau", niveau);
        model.addAttribute("semestre", semestre);
        model.addAttribute("annee", annee);

        return "cahiers/archived/index";
    }

    @GetMapping("/{id}/new_entry")
    public String newEntryPage(@PathVariable("id") UUID id, Model model) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Cahier cahier = cahierRepository.findById(id).orElse(null);

        if (cahier == null)
            return "redirect:/error/404";

        if (!user.getId().equals(cahier.getAffectation().getProf().getId()))
            return "redirect:/error/403";

        model.addAttribute("formAction", "/cahiers/" + id + "/entries");
        model.addAttribute("formMethod", "POST");
        model.addAttribute("previous", "/cahiers/" + id + "/entries");
        model.addAttribute("enabled", user.getRole().equals("ROLE_PROF"));
        model.addAttribute("user", user);
        model.addAttribute("cahier", cahier);
        return "cahiers/cahier";
    }

    @GetMapping("/{id}")
    public String getEntries(
            @PathVariable UUID id, Model model,
            @RequestParam(defaultValue = "0") int page
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Cahier cahier = cahierRepository.findById(id).orElse(null);

        if (cahier == null)
            return "redirect:/error/404";

        if (user.getRole().equals("ROLE_PROF") && !user.getId().equals(cahier.getAffectation().getProf().getId()))
            return "redirect:/error/403";

        Page<Entree> entries = EntreeService.getEntries(page, SIZE, cahier.getEntrees());

        model.addAttribute("user", user);
        model.addAttribute("cahier", cahier);
        model.addAttribute("entries", entries);

        return "cahiers/entries/index";

    }

    @PostMapping("/{id}/entries")
    public String createEntry(@PathVariable("id") UUID id, @ModelAttribute Entree entry, Model model, RedirectAttributes redAtts) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        try {
            Cahier cahier = cahierRepository.findById(id).orElse(null);

            if (cahier == null)
                return "redirect:/error/404";

            if (!user.getId().equals(cahier.getAffectation().getProf().getId()))
                return "redirect:/error/403";

            int semaine = 1;

            if (cahier.getEntrees() == null)
                cahier.setEntrees(new ArrayList<>());
            else
                semaine = !cahier.getEntrees().isEmpty() ? cahier.getEntrees().size() + 1 : 1;

            List<Objectif> objectifs = entry.getObjectifs();
            entry.setObjectifs(new ArrayList<>());
            entry.setId(null);
            entry.setSeance(semaine);
            entry.setCahier(cahier);

            cahier.addEntree(entry);

            Cahier savedCahier = cahierRepository.save(cahier);
            Entree savedEntry = savedCahier.getEntrees().get(savedCahier.getEntrees().size() - 1);

            for (Objectif obj : objectifs) {
                if (obj.getContenue() == null || obj.getContenue().isBlank())
                    continue;

                obj.setEntree(savedEntry);
                savedEntry.addObjectif(obj);
            }

            savedCahier = cahierRepository.save(cahier);
            savedEntry = savedCahier.getEntrees().get(savedCahier.getEntrees().size() - 1);

            redAtts.addFlashAttribute("error", false);
            redAtts.addFlashAttribute("msg", "Entrée créer avec succès");

            return "redirect:/cahiers/" + id + "/entries/" + savedEntry.getId();
        } catch (Exception e) {
            log.error("Error creating entry: \n{}", String.valueOf(e));redAtts.addFlashAttribute("error", false);
        }

        model.addAttribute("msg", "L'entrée n'as pas été enregistré. Veuillez réessayer plutard.");
        model.addAttribute("error", true);
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/cahiers/" + id + "/entries");
        model.addAttribute("formMethod", "POST");
        return "cahiers/cahier";
    }

    @GetMapping("/{id}/entries/{entryId}")
    public String getEntry(@PathVariable("id") UUID id, @PathVariable("entryId") UUID entryId, Model model) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Cahier cahier = cahierRepository.findById(id).orElse(null);

        if (cahier == null)
            return "redirect:/error/404";

        if (user.getRole().equals("ROLE_PROF") && !user.getId().equals(cahier.getAffectation().getProf().getId()))
            return "redirect:/error/403";

        Entree entry = entreeRepository.findById(entryId).orElse(null);

        if (entry == null)
            return "redirect:/error/404";

        model.addAttribute("previous", "/cahiers/" + id + "/entries");
        model.addAttribute("enabled", user.getRole().equals("ROLE_PROF"));
        model.addAttribute("user", user);
        model.addAttribute("cahier", cahier);
        model.addAttribute("entry", entry);
        model.addAttribute("formAction", "/cahiers/" + id + "/entries/" + entryId);
        model.addAttribute("formMethod", "PUT");

        return "cahiers/cahier";
    }

    @PutMapping("/{id}/entries/{entryId}")
    public String editEntry(@PathVariable("id") UUID id, @PathVariable("entryId") UUID entryId, @ModelAttribute Entree entry, RedirectAttributes redAtts) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Cahier cahier = cahierRepository.findById(id).orElse(null);

        if (cahier == null)
            return "redirect:/error/404";

        if (!user.getId().equals(cahier.getAffectation().getProf().getId()))
            return "redirect:/error/403";

        try {
            Entree e = cahier
                    .getEntrees()
                    .stream()
                    .filter(entree -> entree.getId().equals(entryId))
                    .findFirst()
                    .orElse(null);

            if (e == null)
                return "redirect:/error/404";

            e.setDescription(entry.getDescription());
            e.setDate(entry.getDate());
            e.setHeureDebut(entry.getHeureDebut());
            e.setHeureFin(entry.getHeureFin());
            e.setNature(entry.getNature());

            List<Objectif> objectives = new ArrayList<>();

            for (Objectif obj : entry.getObjectifs()) {
                if (obj.getContenue() == null || obj.getContenue().isBlank())
                    continue;

                obj.setEntree(e);
                objectives.add(obj);
            }

            e.getObjectifs().clear();

            for (Objectif obj : objectives)
                e.addObjectif(obj);

            cahierRepository.save(cahier);
            redAtts.addFlashAttribute("error", false);
            redAtts.addFlashAttribute("msg", "Entrée modifiée avec succès");
        } catch (Exception e) {
            log.error(e.getMessage());
            redAtts.addFlashAttribute("msg", "L'entrée n'as pas été enregistré. Veuillez réessayer plutard.");
            redAtts.addFlashAttribute("error", true);
        }

        return "redirect:/cahiers/" + id + "/entries/" + entryId;
    }

    @DeleteMapping("/{id}")
    public String deleteCahier(@PathVariable("id") UUID id, RedirectAttributes redAtts){
        Cahier cahier = cahierRepository.findById(id).orElse(null);

        if (cahier == null)
            return "redirect:/error/404";

        try {
            cahierRepository.delete(cahier);
            redAtts.addFlashAttribute("error", false);
        } catch (Exception e) {
            log.error(e.getMessage());
            redAtts.addFlashAttribute("error", true);
        }

        return "redirect:/cahiers/archive";
    }
}
