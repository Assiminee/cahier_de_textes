package upf.pjt.cahier_de_textes.dao.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "filiere")
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "intitule", nullable = false)
    private String intitule;

    @Min(2)
    @Max(5)
    @Column(name = "nombre_annees", nullable = false)
    private int nombreAnnees;

    @NotNull
    @Column(name = "date_reconnaissance", nullable = false)
    private LocalDate dateReconnaissance;

    @NotNull
    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "diplome", nullable = false)
    private Diplome diplome;

    @OneToOne
    @JoinColumn(name = "coordinateur", unique = true)
    private Professeur coordinateur;

    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Affectation> affectations;
}
