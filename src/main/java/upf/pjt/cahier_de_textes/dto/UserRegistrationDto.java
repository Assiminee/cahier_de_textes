package upf.pjt.cahier_de_textes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.entities.validation_annotations.IsAdult;

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
    @Pattern(regexp = "\\+212[6-7][0-9]{8}", message = "Must be a valid Moroccan phone number")
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
}
