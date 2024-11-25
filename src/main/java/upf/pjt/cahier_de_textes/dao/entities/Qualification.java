package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@ToString
@NoArgsConstructor
@Getter
@Setter
@Table(name = "qualification")
public class Qualification {

    public Qualification(String intitule, LocalDate dateObtention, Professeur prof) {
        this.intitule = intitule;
        this.dateObtention = dateObtention;
        this.prof = prof;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "intitule", nullable = false)
    private String intitule;

    @NotNull
    @Column(name = "date_obtention", nullable = false)
    private LocalDate dateObtention;

    @ManyToOne
    @JoinColumn(name = "prof")
    @JsonBackReference
    private Professeur prof;
}
