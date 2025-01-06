package upf.pjt.cahier_de_textes.dao.dtos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Qualification;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProfEditDto {
    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private Genre genre;
    private LocalDate dateNaissance;
    private String cin;
    private String telephone;
    private Grade grade;
    private LocalDate dateDernierDiplome;
    private LocalDate dateEmbauche;
    private String pwd;
    private List<Qualification> qualifications;


    public void updateEntity(Professeur existingProf, BCryptPasswordEncoder encoder) {
        existingProf.setNom(nom);
        existingProf.setPrenom(prenom);
        existingProf.setEmail(email);
        existingProf.setAdresse(adresse);
        existingProf.setGenre(genre);
        existingProf.setDateNaissance(dateNaissance);
        existingProf.setCin(cin);
        existingProf.setTelephone(telephone);
        existingProf.setGrade(grade);
        existingProf.setDateDernierDiplome(dateDernierDiplome);
        existingProf.setDateEmbauche(dateEmbauche);

        // Update password only if not blank
        if (pwd != null && !pwd.trim().isEmpty()) {
            existingProf.setPwd(encoder.encode(pwd));
        }

        // Update qualifications
        if (qualifications != null) {
            existingProf.getQualifications().clear();
            for (Qualification qualification : qualifications) {
                qualification.setProf(existingProf);
                existingProf.getQualifications().add(qualification);
            }
        }
    }

    public static ProfEditDto fromEntity(Professeur professeur) {
        ProfEditDto dto = new ProfEditDto();
        dto.setNom(professeur.getNom());
        dto.setPrenom(professeur.getPrenom());
        dto.setEmail(professeur.getEmail());
        dto.setAdresse(professeur.getAdresse());
        dto.setGenre(professeur.getGenre());
        dto.setDateNaissance(professeur.getDateNaissance());
        dto.setCin(professeur.getCin());
        dto.setTelephone(professeur.getTelephone());
        dto.setGrade(professeur.getGrade());
        dto.setDateDernierDiplome(professeur.getDateDernierDiplome());
        dto.setDateEmbauche(professeur.getDateEmbauche());
        dto.setPwd(professeur.getPwd());
        dto.setQualifications(
                professeur.getQualifications() != null
                        ? professeur.getQualifications().stream().collect(Collectors.toList())
                        : null
        );
        return dto;
    }
}
