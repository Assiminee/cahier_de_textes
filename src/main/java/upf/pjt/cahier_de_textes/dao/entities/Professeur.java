package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @OneToMany(mappedBy = "responsable", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();

    @Setter
    @Getter
    @NotNull
    @HasAtLeastOneQualification
    @OneToMany(mappedBy = "prof", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Qualification> qualifications = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "coordinateur")
    @JsonManagedReference
    private Filiere filiere;
}
