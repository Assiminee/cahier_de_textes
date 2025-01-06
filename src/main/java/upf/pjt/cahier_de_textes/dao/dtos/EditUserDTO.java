package upf.pjt.cahier_de_textes.dao.dtos;

import ch.qos.logback.core.encoder.EchoEncoder;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EditUserDTO extends UserDTO {
    private LocalDate dateDernierDiplome; // Only for professors
    private LocalDate dateEmbauche;
    private String password;// Only for professors

    public EditUserDTO(String nom, String prenom, String genre, String cin, String adresse, String telephone, String email, String dateNaissance, String role,Grade grade, LocalDate dateDernierDiplome, LocalDate dateEmbauche) {
        super(nom, prenom, genre, cin, adresse, telephone, email, dateNaissance);
        this.role = role;
        this.dateDernierDiplome = dateDernierDiplome;
        this.dateEmbauche = dateEmbauche;
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
