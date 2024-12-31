package upf.pjt.cahier_de_textes.dao.dtos.filiere;

import lombok.*;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Jour;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class SaveEditAffectationDTO {
    private UUID id;
    private int niveau;
    private int semestre;
    private String heureDebut;
    private String heureFin;
    private Jour jour;
    private UUID professeur;
    private UUID module;
}
