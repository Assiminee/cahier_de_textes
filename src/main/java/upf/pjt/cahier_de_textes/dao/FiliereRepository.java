package upf.pjt.cahier_de_textes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import upf.pjt.cahier_de_textes.entities.Filiere;
import upf.pjt.cahier_de_textes.entities.Professeur;

import java.util.UUID;

public interface FiliereRepository extends  JpaRepository<Filiere, UUID> {
    /*void deleteAllByProf(Professeur prof);*/
}
