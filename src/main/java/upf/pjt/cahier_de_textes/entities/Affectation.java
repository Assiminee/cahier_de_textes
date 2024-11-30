package upf.pjt.cahier_de_textes.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.entities.validation.annotations.IsValidNiveau;
import upf.pjt.cahier_de_textes.entities.validation.annotations.IsValidSemestre;

import java.util.UUID;

@Entity
@Table(name = "affectation")
@IsValidNiveau
@IsValidSemestre
public class Affectation {
    public Affectation() {}

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Setter
    @Min(1)
    @Max(5)
    @Column(name = "niveau", nullable = false)
    private int niveau;

    @Getter
    @Setter
    @Min(1)
    @Max(5)
    @NotNull
    @Column(name = "semestre", nullable = false)
    private int semestre;

    @Getter
    @Setter
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "filiere", nullable = false)
    private Filiere filiere;

    @Getter
    @Setter
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "prof", nullable = false)
    private Professeur prof;

    @Getter
    @Setter
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "module", nullable = false)
    private Module module;
}
