package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.HasAtLeastOneQualification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "professeur")
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private Grade grade;

    @NotNull
    @Column(name = "date_dernier_diplome", nullable = false)
    private LocalDate dateDernierDiplome;

    @NotNull
    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;

    @OneToMany(mappedBy = "responsable")
    @JsonIgnore
    private List<Module> modules = new ArrayList<>();

    @NotNull
    @HasAtLeastOneQualification
    @OneToMany(mappedBy = "prof", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Qualification> qualifications = new ArrayList<>();

    @OneToOne(mappedBy = "coordinateur")
    @JsonIgnore
    private Filiere filiere;

    @OneToMany(mappedBy = "prof", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Affectation> affectations;

    @Override
    public String toString() {
        return "Professeur{" +
                "grade=" + grade +
                ", dateDernierDiplome=" + dateDernierDiplome +
                ", dateEmbauche=" + dateEmbauche +
                ", modules=" + modules +
                ", qualifications=" + qualifications +
                ", filiere=" + (filiere == null ? "null" : filiere.getIntitule()) +
                ", affectations=" + affectations +
                '}';
    }
}
