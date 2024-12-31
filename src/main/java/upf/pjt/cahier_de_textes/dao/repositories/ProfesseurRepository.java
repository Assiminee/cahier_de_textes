package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfesseurRepository extends  JpaRepository<Professeur, UUID> {
    List<Professeur> findAllByFiliereIsNull();

    @Query("SELECT p.grade FROM Professeur p WHERE p.id = :id")
    Grade getProfesseurGrade(@Param("id") UUID id);

    @Query("SELECT p FROM Professeur p " +
            "LEFT JOIN FETCH p.affectations a " +
            "GROUP BY p HAVING COUNT(a) < 8"
    )
    List<Professeur> getAvailableProfesseurs();
}
