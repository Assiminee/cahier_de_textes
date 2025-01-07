package upf.pjt.cahier_de_textes.dao.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.IsAdult;

import java.time.LocalDate;

@Getter
@Setter
public class UserRegistrationDto {
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private String adresse;
    private Genre genre;
    private String cin;
    private String password;
    private String confirmPassword;
    private String role;

    public UserRegistrationDto(
            String nom
            , String prenom
            , String telephone
            , String email
            , LocalDate dateNaissance
            , String adresse
            , Genre genre
            , String cin
            , String password
            , String confirmPassword
            , String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.genre = genre;
        this.cin = cin;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }

    public void RegisterUser(User user) {
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setGenre(genre);
        user.setCin(cin);
        user.setAdresse(adresse);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setDateNaissance(dateNaissance);
    }

}
