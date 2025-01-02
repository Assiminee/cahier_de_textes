package upf.pjt.cahier_de_textes.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.FiliereDTO;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import upf.pjt.cahier_de_textes.dao.repositories.FiliereRepository;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilieresService {
    private final FiliereRepository filiereRepository;

    public FilieresService(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    public static Map<String, String> packageParams(
            String intitule, String coordinateur, String diplome, Boolean reconnaissance
    ) {
        Map<String, String> params = new HashMap<>();

        params.put("intitule", intitule);
        params.put("coordinateur", coordinateur);
        params.put("diplome", diplome);
        params.put("reconnaissance", reconnaissance != null ? String.valueOf(reconnaissance) : "");

        return params;
    }

    public Page<FiliereDTO> mapFilieres(
            Boolean reconnaissance, String intitule, String coordinateur, Diplome diplome, Pageable pageable
    ) {
        Page<Filiere> rawFilieres = filiereRepository.getFilieres(intitule, coordinateur, diplome, reconnaissance, pageable);

        return rawFilieres.map(FiliereDTO::new);
    }

    public Boolean duplicateData(Filiere filiere, String method, RedirectAttributes redAtts) {
        boolean error;

        try {
            Boolean duplicateIntitule = false;
            Boolean duplicateCoordinateur = false;

            if (method.equals("post")) {
                duplicateIntitule = filiereRepository.existsByIntitule(filiere.getIntitule());
                duplicateCoordinateur = filiereRepository.existsByCoordinateur(filiere.getCoordinateur());
            } else if (method.equals("put")) {
                duplicateIntitule = filiereRepository.existsByIdIsNotAndIntitule(filiere.getId(), filiere.getIntitule());
                duplicateCoordinateur = filiereRepository.existsByIdIsNotAndCoordinateur(filiere.getId(), filiere.getCoordinateur());
            }

            error = duplicateIntitule || duplicateCoordinateur;

            if (duplicateIntitule)
                redAtts.addFlashAttribute("duplicateIntitule", filiere.getIntitule());

            if (duplicateCoordinateur)
                redAtts.addFlashAttribute("duplicateCoordinateur", filiere.getCoordinateur().getFullName());

        } catch (Exception e) {
            error = true;
            log.error(String.valueOf(e.getCause()));
            redAtts.addFlashAttribute("msg", method.equals("post") ? "L'ajout " : "La modification " + "de la filiere a échouée");
        }

        redAtts.addFlashAttribute("error", error);
        return error;
    }
}
