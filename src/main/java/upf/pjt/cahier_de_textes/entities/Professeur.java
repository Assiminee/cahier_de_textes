package upf.pjt.cahier_de_textes.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.entities.validation_annotations.HasAtLeastOneQualification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Entity
@Table(name = "professeur")
@PrimaryKeyJoinColumn(name = "id")
public class Professeur extends User {

    public Professeur() {
        this.role = new Role(RoleEnum.ROLE_PROF);
    }

    public Professeur(
            String nom, String prenom, String telephone, String email,
            LocalDate dateNaissance, String adresse, Genre genre,
            String cin, String pwd, Grade grade,
            LocalDate dateDernierDiplome, LocalDate dateEmbauche
    ) {
        super(nom, prenom, telephone, email, dateNaissance, adresse, genre, cin, pwd, new Role(RoleEnum.ROLE_PROF));
        this.grade = grade;
        this.dateDernierDiplome = dateDernierDiplome;
        this.dateEmbauche = dateEmbauche;
    }

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private Grade grade;

    @Setter
    @NotNull
    @Column(name = "date_dernier_diplome", nullable = false)
    private LocalDate dateDernierDiplome;

    @Setter
    @NotNull
    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;

    @Setter
    @OneToMany(mappedBy = "responsable")
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();

    @Setter
    @Getter
    @NotNull
    @OneToMany(mappedBy = "prof", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Qualification> qualifications = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "coordinateur")
    @JsonManagedReference
    private Filiere filiere;

    @OneToMany(mappedBy = "prof")
    @JsonManagedReference
    private List<Affectation> affectations = new ArrayList<>();
}
