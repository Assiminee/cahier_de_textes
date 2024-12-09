package upf.pjt.cahier_de_textes.dao.dtos;

import lombok.*;
import org.springframework.stereotype.Component;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Component
public class EditUserDTO {
    private String nom;
    private String prenom;
    private Genre genre;
    private String cin;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private Grade grade;
    private RoleEnum role;

    public EditUserDTO(String nom, String prenom, String genre, String cin, String adresse, String telephone, String email, String dateNaissance, String grade, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = Genre.valueOf(genre);
        this.cin = cin;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = LocalDate.parse(dateNaissance);
        this.grade = Grade.valueOf(grade);
        this.role = RoleEnum.valueOf(role);
    }

    public void setUserDetails(User user) {
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setGenre(genre);
        user.setCin(cin);
        user.setAdresse(adresse);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setDateNaissance(dateNaissance);
    }

    public void setProfDetails(Professeur prof) {
        setUserDetails(prof);
        prof.setGrade(grade);
    }
}
