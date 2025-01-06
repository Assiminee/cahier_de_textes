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

    @NotBlank(message = "Nom is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Nom must contain only letters")
    private String nom;

    @NotBlank(message = "Prenom is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Prenom must contain only letters")
    private String prenom;

    @NotBlank(message = "Telephone is required")
    @Pattern(regexp = "(\\+212|0)[6-7][0-9]{8}", message = "Must be a valid Moroccan phone number")
    private String telephone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Date de Naissance is required")
    @IsAdult(message = "User must be at least 18 years old")
    private LocalDate dateNaissance;

    @NotBlank(message = "Adresse is required")
    private String adresse;

    @NotNull(message = "Genre is required")
    private Genre genre;

    @NotBlank(message = "CIN is required")
    @Pattern(regexp = "^[A-Za-z]{1,2}[0-9]{4,6}$", message = "Invalid CIN format")
    private String cin;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character"
    )
    private String password;

    @NotBlank(message = "Confirm Password is required")
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
    /*    user.setRole();*/

    }



  /*  @NotNull(message = "Grade is required", groups = ProfessorValidationGroup.class)
    private Grade grade;

    @NotNull(message = "Date Dernier Diplôme is required for professors", groups = ProfessorValidationGroup.class)
    private LocalDate dateDernierDiplome;

    @NotNull(message = "Date d'Embauche is required for professors", groups = ProfessorValidationGroup.class)
    private LocalDate dateEmbauche;

    // Prof-specific qualifications
    private List<QualificationDto> qualifications;

    @Null(message = "ID should be null", groups = CommonValidationGroup.class)
    private UUID id;*/

}
