package upf.pjt.cahier_de_textes.dao.dtos.filiere;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class FiliereDTO {
    private UUID id;
    private String intitule;
    private int nombreAnnees;
    private LocalDate dateReconnaissance;
    private LocalDate dateExpiration;
    private Diplome diplome;
    private ProfesseurDTO coordinateur;
    private String affectations;


    public FiliereDTO(Filiere filiere) {
        this.id = filiere.getId();
        this.intitule = filiere.getIntitule();
        this.nombreAnnees = filiere.getNombreAnnees();
        this.dateReconnaissance = filiere.getDateReconnaissance();
        this.dateExpiration = filiere.getDateExpiration();
        this.diplome = filiere.getDiplome();
        this.coordinateur = filiere.getCoordinateur() != null ? new ProfesseurDTO(filiere.getCoordinateur()) : null;
    }

    public void setAffectations(Filiere filiere, ObjectMapper objectMapper) throws JsonProcessingException {
        this.affectations = mapAffectations(filiere, objectMapper);
    }

    private String mapAffectations(Filiere filiere, ObjectMapper objectMapper) throws JsonProcessingException {
        Map<String, Map<String, List<AffectationDTO>>> affectations = new HashMap<>();

        for (Affectation affectation : filiere.getAffectations()) {
            String niveau = "N-" + affectation.getNiveau();
            String semestre = "S-" + affectation.getSemestre();

            if (!affectations.containsKey(niveau))
                affectations.put(niveau, new HashMap<>());

            Map<String, List<AffectationDTO>> map = affectations.get(niveau);

            if (!map.containsKey(semestre))
                map.put(semestre, new ArrayList<>());

            map.get(semestre).add(new AffectationDTO(affectation));
        }

        return objectMapper.writeValueAsString(affectations);
    }
}
