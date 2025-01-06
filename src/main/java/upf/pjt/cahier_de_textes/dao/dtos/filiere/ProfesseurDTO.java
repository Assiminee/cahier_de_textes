package upf.pjt.cahier_de_textes.dao.dtos.filiere;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class ProfesseurDTO {
    private UUID id;
    private String nom;
    private String prenom;

    public ProfesseurDTO(Professeur prof) {
        this.id = prof.getId();
        this.nom = prof.getNom();
        this.prenom = prof.getPrenom();
    }

    public ProfesseurDTO(UUID id) {
        this.id = id;
    }

    public String getFullName() { return nom + " " + prenom; }
}
