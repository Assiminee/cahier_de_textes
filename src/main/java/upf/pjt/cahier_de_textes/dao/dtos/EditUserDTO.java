package upf.pjt.cahier_de_textes.dao.dtos;

import ch.qos.logback.core.encoder.EchoEncoder;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EditUserDTO {
    private String nom;
    private String prenom;
    private Genre genre;
    private String cin;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private RoleEnum role;
    private Grade grade; // Only for professors
    private LocalDate dateDernierDiplome; // Only for professors
    private LocalDate dateEmbauche;
    private String password;// Only for professors

    public EditUserDTO(String nom, String prenom, String genre, String cin, String adresse, String telephone, String email, String dateNaissance, String role,Grade grade, LocalDate dateDernierDiplome, LocalDate dateEmbauche) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = Genre.valueOf(genre);
        this.cin = cin;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = LocalDate.parse(dateNaissance);
        this.role = RoleEnum.valueOf(role);
        this.grade = Grade.valueOf(role);
        this.dateDernierDiplome = LocalDate.now();
        this.dateEmbauche = LocalDate.now();
    }
    public EditUserDTO(String nom, String prenom, String genre, String cin, String adresse, String telephone, String email, String dateNaissance, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = Genre.valueOf(genre);
        this.cin = cin;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = LocalDate.parse(dateNaissance);

        this.role = RoleEnum.valueOf(role);
    }
    public void setUserDetails(User user) {
        user.setNom(nom);
        user.setPrenom(prenom);

        if (genre != null) {
            try {
                user.setGenre(Genre.valueOf(genre.name()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid genre value: " + genre);
            }
        }

        user.setCin(cin);
        user.setAdresse(adresse);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setDateNaissance(dateNaissance);

        try {
            if (password != null && !password.isBlank()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                user.setPwd(encoder.encode(password));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
