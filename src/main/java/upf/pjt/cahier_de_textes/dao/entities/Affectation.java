package upf.pjt.cahier_de_textes.dao.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Jour;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.IsValidNiveau;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.IsValidSemestre;
import java.util.UUID;

@Entity
@Table(name = "affectation")
@IsValidNiveau
@IsValidSemestre
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Affectation {

    public Affectation(UUID id, int niveau, int semestre, int heureDebut, int heureFin, Jour jour, Filiere filiere, Professeur prof, Module module) {
        this.id = id;
        this.niveau = niveau;
        this.semestre = semestre;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.jour = jour;
        this.filiere = filiere;
        this.prof = prof;
        this.module = module;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Min(1)
    @Max(5)
    @Column(name = "niveau", nullable = false)
    private int niveau;

    @NotNull
    @Column(name = "semestre", nullable = false)
    private int semestre;

    @NotNull
    @Column(name = "heure_debut", nullable = false)
    private int heureDebut;

    @NotNull
    @Column(name = "heure_fin", nullable = false)
    private int heureFin;

    @NotNull
    @Column(name = "jour", nullable = false)
    @Enumerated(EnumType.STRING)
    private Jour jour;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere", nullable = false)
    private Filiere filiere;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "prof", nullable = false)
    private Professeur prof;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "module", nullable = false)
    private Module module;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cahier", unique = true)
    private Cahier cahier;

    @Override
    public String toString() {
        return "Affectation{" +
                "id=" + id +
                ", niveau=" + niveau +
                ", semestre=" + semestre +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", jour=" + jour +
                ", filiere=" + filiere.getIntitule() +
                ", prof=" + prof.getFullName() +
                ", module=" + module.getIntitule() +
                ", cahier=" + cahier +
                '}';
    }
}
