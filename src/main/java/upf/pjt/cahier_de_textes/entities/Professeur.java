package upf.pjt.cahier_de_textes.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.entities.validation.annotations.HasAtLeastOneQualification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professeur")
public class Professeur extends User {
    public Professeur() {}

    @Getter
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private Grade grade;

    @Getter
    @Setter
    @NotNull
    @Column(name = "date_dernier_diplome", nullable = false)
    private LocalDate dateDernierDiplome;

    @Getter
    @Setter
    @NotNull
    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;

    @Getter
    @Setter
    @OneToMany(mappedBy = "responsable", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();

    @Getter
    @Setter
    @NotNull
    @HasAtLeastOneQualification
    @OneToMany(mappedBy = "prof", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Qualification> qualifications = new ArrayList<>();

    @Getter
    @Setter
    @OneToOne(mappedBy = "coordinateur")
    @JsonManagedReference
    private Filiere filiere;
}
