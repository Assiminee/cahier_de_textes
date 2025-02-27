package upf.pjt.cahier_de_textes.dao.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.IsAdult;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user")
public class User {

    public User(
            String nom, String prenom, String telephone, String email,
            LocalDate dateNaissance, String adresse, Genre genre,
            String cin, String pwd, Role role
    ) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.genre = genre;
        this.cin = cin;
        this.pwd = pwd;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+([ -][a-zA-Z]+)*$")
    @Column(name = "nom", nullable = false)
    protected String nom;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+([ -][a-zA-Z]+)*$")
    @Column(name = "prenom", nullable = false)
    protected String prenom;

    @NotBlank
    @Pattern(regexp = "\\+212[6-7][0-9]{8}", message = "Must be a valid Moroccan phone number")
    @Column(name = "telephone", nullable = false, unique = true)
    protected String telephone;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    protected String email;

    @NotNull
    @IsAdult
    @Column(name = "date_naissance", nullable = false)
    protected LocalDate dateNaissance;

    @NotBlank
    @Column(name = "adresse", nullable = false)
    protected String adresse;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    protected Genre genre;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{1,2}[0-9]{4,6}$")
    @Column(name = "cin", nullable = false, unique = true)
    protected String cin;

    @NotBlank
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    @Column(name = "pwd", nullable = false)
    protected String pwd;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonManagedReference
    protected Role role;

    public String getAuthority(){
        return role.getAuthority();
    }
    public String getFullName() {
        return nom + " " + prenom;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", adresse='" + adresse + '\'' +
                ", genre=" + genre +
                ", cin='" + cin + '\'' +
                ", pwd='" + pwd + '\'' +
                ", role=" + role.getAuthority() +
                '}';
    }
}
