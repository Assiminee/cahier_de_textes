package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.ModeEval;

import java.util.UUID;

@Entity
@Table(name = "module")
public class Module {
    public Module() {}

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Setter
    @NotBlank
    @Column(name = "intitule", nullable = false)
    private String intitule;

    @Getter
    @Setter
    @NotNull
    @Column(name = "nombre_heures", nullable = false)
    private int nombre_heures;


    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_evaluation", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'EXAM'")
    private ModeEval modeEvaluation;

    public String getModeEvaluation() {
        return modeEvaluation.toString();
    }


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "responsable")
    @JsonBackReference
    private Professeur responsable;

    public String getResponsableNom() {
        return responsable.nom;
    }
}
