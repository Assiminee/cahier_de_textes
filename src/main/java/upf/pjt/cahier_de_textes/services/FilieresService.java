package upf.pjt.cahier_de_textes.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.dtos.filiere.FiliereDTO;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import upf.pjt.cahier_de_textes.dao.repositories.FiliereRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class FilieresService {
    private final FiliereRepository filiereRepository;
    private final ObjectMapper objectMapper;

    public FilieresService(FiliereRepository filiereRepository, ObjectMapper objectMapper) {
        this.filiereRepository = filiereRepository;
        this.objectMapper = objectMapper;
    }

    public static Map<String, String> packageParams(
            String intitule, String coordinateur, String diplome, boolean reconnaissance
    ) {
        Map<String, String> params = new HashMap<>();

        params.put("intitule", intitule);
        params.put("coordinateur", coordinateur);
        params.put("diplome", diplome);
        params.put("reconnaissance", String.valueOf(reconnaissance));

        return params;
    }

    public Page<FiliereDTO> mapFilieres(
            boolean reconnaissance, String intitule, String coordinateur, Diplome diplome, Pageable pageable
    ) {
        Page<Filiere> rawFilieres;

        if (reconnaissance)
            rawFilieres = filiereRepository.recognizedFilieres(intitule, coordinateur, diplome, pageable);
        else
            rawFilieres = filiereRepository.expiredFilieres(intitule, coordinateur, diplome, pageable);

        Page<FiliereDTO> filieres = rawFilieres.map(filiere -> {
            try {
                return new FiliereDTO(filiere, objectMapper);
            } catch (JsonProcessingException e) {
                return null;
            }
        });

        if (filieres.stream().anyMatch(Objects::isNull))
            return null;

        return filieres;
    }
}
