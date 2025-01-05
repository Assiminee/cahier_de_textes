package upf.pjt.cahier_de_textes.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.entities.Cahier;
import upf.pjt.cahier_de_textes.dao.entities.Entree;
import upf.pjt.cahier_de_textes.dao.entities.Objectif;
import upf.pjt.cahier_de_textes.dao.repositories.CahierRepository;
import upf.pjt.cahier_de_textes.dao.repositories.EntreeRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ObjectifRepository;
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
    private final ObjectifRepository objectifRepository;
    private final EntreeRepository entreeRepository;

    public CahierController(CahierRepository cahierRepository, ObjectifRepository objectifRepository, EntreeRepository entreeRepository) {
        this.cahierRepository = cahierRepository;
        this.objectifRepository = objectifRepository;
        this.entreeRepository = entreeRepository;
    }

    @GetMapping("/{id}/new_entry")
    public String cahiers(@PathVariable("id") UUID id, Model model) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        model.addAttribute("user", user);
        model.addAttribute("formAction", "/cahiers/" + id + "/entries");
        model.addAttribute("formMethod", "POST");
        return "cahiers/cahier";
    }

    @GetMapping("/{id}")
    public String cahiers(
            @PathVariable UUID id, Model model,
            @RequestParam(defaultValue = "0") int page
    ) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        Cahier cahier = cahierRepository.findById(id).orElse(null);

        if (cahier == null)
            return "redirect:/error/404";

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


            int semaine = 1;

            if (cahier.getEntrees() == null)
                cahier.setEntrees(new ArrayList<>());
            else
                semaine = !cahier.getEntrees().isEmpty() ? cahier.getEntrees().size() : 1;

            List<Objectif> objectifs = entry.getObjectifs();
            entry.setObjectifs(new ArrayList<>());
            entry.setId(null);
            entry.setSeance(semaine);
            entry.setCahier(cahier);

            cahier.addEntree(entry);

            Cahier savedCahier = cahierRepository.save(cahier);
            Entree savedEntry = savedCahier.getEntrees().get(savedCahier.getEntrees().size() - 1);

            for (Objectif obj : objectifs) {
                obj.setEntree(savedEntry);
                savedEntry.addObjectif(obj);
            }

            savedCahier = cahierRepository.save(cahier);
            savedEntry = savedCahier.getEntrees().get(savedCahier.getEntrees().size() - 1);

            System.out.println(savedEntry);

            return "redirect:/cahiers/" + id + "/entries/" + savedEntry.getId();
        } catch (Exception e) {
            log.error("Error creating entry: \n{}", String.valueOf(e));
        }

        model.addAttribute("user", user);
        model.addAttribute("formAction", "/cahiers/" + id + "/entries");
        model.addAttribute("formMethod", "POST");
        return "cahiers/cahier";
    }

    @GetMapping("/{id}/entries/{entryId}")
    public String getEntry(@PathVariable("id") UUID id, @PathVariable("entryId") UUID entryId, Model model, RedirectAttributes redAtts) {
        UserDTO user = UserService.getAuthenticatedUser();

        if (user == null)
            return "redirect:/error/401";

        boolean cahierExists = cahierRepository.existsById(id);

        if (!cahierExists)
            return "redirect:/error/404";

        Entree entry = entreeRepository.findById(entryId).orElse(null);

        if (entry == null)
            return "redirect:/error/404";

        System.out.println(entry);

        model.addAttribute("user", user);
        model.addAttribute("entry", entry);
        model.addAttribute("formAction", "/cahiers/" + id + "/entries/" + entryId);
        model.addAttribute("formMethod", "PUT");

        return "cahiers/cahier";
    }
}
