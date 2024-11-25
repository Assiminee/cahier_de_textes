package upf.pjt.cahier_de_textes.dao.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.IsValidNiveau;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.IsValidSemestre;
import java.util.UUID;

@Entity
@Table(name = "affectation")
@IsValidNiveau
@IsValidSemestre
@NoArgsConstructor
@Getter
@Setter
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Min(1)
    @Max(5)
    @Column(name = "niveau", nullable = false)
    private int niveau;

    @Min(1)
    @Max(5)
    @NotNull
    @Column(name = "semestre", nullable = false)
    private int semestre;

    @NotNull
    @ManyToOne(optional = false)
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

    @NotNull
    @Column(name = "heure_debut", nullable = false)
    private int heureDebut;

    @NotNull
    @Column(name = "heure_fin", nullable = false)
    private int heureFin;

    @NotBlank
    @Column(name = "jour", nullable = false)
    private String jour;
}
