package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
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

    @Column(name = "date_reconnaissance")
    private LocalDate dateReconnaissance;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "diplome", nullable = false)
    private Diplome diplome;

    @OneToOne
    @JoinColumn(name = "coordinateur", unique = true)
    @JsonBackReference
    private Professeur coordinateur;

    @OneToMany(mappedBy = "filiere", fetch = FetchType.LAZY)
    private List<Affectation> affectations;
}
