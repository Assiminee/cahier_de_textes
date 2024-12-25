package upf.pjt.cahier_de_textes.dao.dtos.filiere;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Jour;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class AffectationDTO {
    private UUID id;
    private int niveau;
    private int semestre;
    private int heureDebut;
    private int heureFin;
    private Jour jour;
    private ProfesseurDTO professeur;
    private ModuleDTO module;

    public AffectationDTO(Affectation affectation) {
        this.id = affectation.getId();
        this.niveau = affectation.getNiveau();
        this.semestre = affectation.getSemestre();
        this.heureDebut = affectation.getHeureDebut();
        this.heureFin = affectation.getHeureFin();
        this.jour = affectation.getJour();

        this.professeur = new ProfesseurDTO(affectation.getProf());
        this.module = new ModuleDTO(affectation.getModule());
    }
}
