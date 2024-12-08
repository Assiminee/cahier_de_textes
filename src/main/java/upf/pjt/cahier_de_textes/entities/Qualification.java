package upf.pjt.cahier_de_textes.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "qualification")
public class Qualification {
    public Qualification() {}

    public Qualification(String intitule, LocalDate dateObtention, Professeur prof) {
        this.intitule = intitule;
        this.dateObtention = dateObtention;
        this.prof = prof;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @NotBlank
    @Column(name = "intitule", nullable = false)
    @Getter
    @Setter
    private String intitule;

    @NotNull
    @Column(name = "date_obtention", nullable = false)
    @Getter
    @Setter
    private LocalDate dateObtention;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prof")
    @Getter
    @Setter
    @JsonBackReference
    private Professeur prof;
}
