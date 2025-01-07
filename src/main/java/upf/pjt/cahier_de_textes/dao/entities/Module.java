package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.ModeEval;

import java.util.List;
import java.util.UUID;

@Entity @Getter @Setter
@Table(name = "module")
public class Module {
    public Module() {}

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "intitule", nullable = false)
    private String intitule;

    @NotNull
    @Column(name = "nombre_heures", nullable = false)
    private int nombre_heures;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_evaluation", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'EXAM'")
    private ModeEval modeEvaluation;

    @ManyToOne
    @JoinColumn(name = "responsable")
    @JsonBackReference
    private Professeur responsable;

    @OneToMany(mappedBy = "module", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Affectation> affectations;

    public String getResponsableNom() {
        return responsable.nom;
    }

    public String getModeEvaluation() {
        return modeEvaluation.toString();
    }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", intitule='" + intitule + '\'' +
                ", nombre_heures=" + nombre_heures +
                ", modeEvaluation=" + modeEvaluation +
                ", affectations=" + (affectations == null ? "null" : affectations) +
                ", responsable=" + (responsable == null ? "null" : responsable.getFullName()) +
                '}';
    }
}
