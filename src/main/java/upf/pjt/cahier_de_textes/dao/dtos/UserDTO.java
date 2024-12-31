package upf.pjt.cahier_de_textes.dao.dtos;

import lombok.*;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Qualification;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.dao.repositories.AffectationRepository;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.QualificationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private UUID id;
    private String nom;
    private String prenom;
    private Genre genre;
    private String cin;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private List<Qualification> qualifications;
    private Grade grade;


    public UserDTO(String nom, String prenom, String genre, String cin, String adresse, String telephone, String email, String dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = Genre.valueOf(genre);
        this.cin = cin;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.dateNaissance = LocalDate.parse(dateNaissance);
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

    public void setUserDTODetails(User user) {
        this.id = user.getId();
        this.nom = user.getNom();
        this.prenom = user.getPrenom();
        this.genre = user.getGenre();
        this.cin = user.getCin();
        this.adresse = user.getAdresse();
        this.telephone = user.getTelephone();
        this.email = user.getEmail();
        this.dateNaissance = user.getDateNaissance();
    }

    public void setProfessorDetails(QualificationRepository quaRep, ProfesseurRepository profRep) {
        this.qualifications = quaRep.findAllByProfId(this.id);
        this.grade = profRep.getProfesseurGrade(this.id);
    }

    public String getFullName() {
        return nom + " " + prenom;
    }
}
