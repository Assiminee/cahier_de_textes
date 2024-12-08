package upf.pjt.cahier_de_textes.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.entities.enumerations.Diplome;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "filiere")
public class Filiere {
    public Filiere() {}

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
    @Min(2)
    @Max(5)
    @Column(name = "nombre_annees", nullable = false)
    private int nombreAnnees;

    @Getter
    @Setter
    @NotNull
    @Column(name = "date_reconnaissance", nullable = false)
    private LocalDate dateReconnaissance;

    @Getter
    @Setter
    @NotNull
    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @Getter
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "diplome", nullable = false)
    private Diplome diplome;

    @Getter
    @Setter
    @NotNull
    @OneToOne
    @JoinColumn(name = "coordinateur", unique = true)
    @JsonBackReference
    private Professeur coordinateur;
}
