package upf.pjt.cahier_de_textes.dao.dtos.filiere;

import lombok.*;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Jour;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @ToString
public class AffectationDTO {
    private UUID id;
    private UUID filiereId;
    private String filiereIntitule;
    private int niveau;
    private int semestre;
    private int heureDebut;
    private int heureFin;
    private Jour jour;
    private ProfesseurDTO professeur;
    private ModuleDTO module;

    public AffectationDTO(Affectation affectation) {
        this.id = affectation.getId();
        this.filiereId = affectation.getFiliere().getId();
        this.filiereIntitule = affectation.getFiliere().getIntitule();
        this.niveau = affectation.getNiveau();
        this.semestre = affectation.getSemestre();
        this.heureDebut = affectation.getHeureDebut();
        this.heureFin = affectation.getHeureFin();
        this.jour = affectation.getJour();
        this.professeur = new ProfesseurDTO(affectation.getProf());
        this.module = new ModuleDTO(affectation.getModule());
    }

    public AffectationDTO(SaveEditAffectationDTO affectationDTO) {
        this.id = affectationDTO.getId();
        this.niveau = affectationDTO.getNiveau();
        this.semestre = affectationDTO.getSemestre();
        this.heureDebut = parseHeure(affectationDTO.getHeureDebut());
        this.heureFin = parseHeure(affectationDTO.getHeureFin());
        this.jour = affectationDTO.getJour();

        this.professeur = new ProfesseurDTO(affectationDTO.getProfesseur());
        this.module = new ModuleDTO(affectationDTO.getModule());
    }

    private int parseHeure(String heure) {
        heure = heure.replace("0", "");
        heure = heure.replace(":", "");
        heure = heure.replace(" ", "");

        return Integer.parseInt(heure);
    }
}