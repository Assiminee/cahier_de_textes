package upf.pjt.cahier_de_textes.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.FiliereRepository;

import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/filieres")
public class FiliereController {
    private final FiliereRepository filiereRepository;
    private final int SIZE = 10;

    public FiliereController(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    @GetMapping
    public String filiere(
            Model model,
            @RequestParam(required = false, defaultValue = "") String intitule,
            @RequestParam(required = false, defaultValue = "") String coordinateur,
            @RequestParam(required = false, defaultValue = "") String diplome,
            @RequestParam(required = false, defaultValue = "true")  boolean reconnaissance,
            @RequestParam(defaultValue = "0") int page
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails))
            return "redirect:/auth/login";

        User loggedUser = userDetails.getUser();

        if (loggedUser == null || !loggedUser.getRole().getAuthority().equals("ROLE_ADMIN"))
            return "redirect:/auth/login";

        Pageable pageable = PageRequest.of(page, SIZE, Sort.by("intitule").ascending());
        Page<Filiere> filieres;
        Diplome d;

        try {
            d = Diplome.valueOf(diplome);
        } catch (Exception e) {
            d = null;
        }

        if (reconnaissance)
            filieres = filiereRepository.recognizedFilieres(intitule, coordinateur, d, pageable);
        else
            filieres = filiereRepository.expiredFilieres(intitule, coordinateur, d, pageable);

        List<Diplome> diplomes = List.of(Diplome.values());
        Map<String, String> packagedParams = packageParams(intitule, coordinateur, diplome, reconnaissance);

        model.addAttribute("params", packagedParams);
        model.addAttribute("user", loggedUser);
        model.addAttribute("filieres", filieres);
        model.addAttribute("diplomes", diplomes);

        return "Admin/filiere/filiere";
    }

    public Map<String, String> packageParams(
            String intitule, String coordinateur, String diplome, boolean reconnaissance
    ) {
        Map<String, String> params = new HashMap<>();

        params.put("intitule", intitule);
        params.put("coordinateur", coordinateur);
        params.put("diplome", diplome);
        params.put("reconnaissance", String.valueOf(reconnaissance));

        return params;
    }
}
