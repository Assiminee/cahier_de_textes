package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.ModeEval;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "module")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @NotBlank
    @Column(name = "intitule", nullable = false)
    private String intitule;

    @NotNull
    @Column(name = "nombre_heures", nullable = false)
    private int nombre_heures;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_evaluation", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'EXAM'")
    private ModeEval modeEvaluation;

    @ManyToOne
    @JoinColumn(name = "responsable")
    @JsonBackReference
    private Professeur responsable;
}
