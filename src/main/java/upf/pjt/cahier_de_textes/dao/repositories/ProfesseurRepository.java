package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;

import java.util.HashMap;
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

    @Query("SELECT p, COALESCE(SUM(m.nombre_heures), 0) " +
            "FROM Professeur p " +
            "LEFT JOIN FETCH p.affectations a " +
            "LEFT JOIN a.module m " +
            "WHERE p = :prof"
    )
    Object getTotalHoursTaught(@Param("prof") Professeur prof);

    @Query("SELECT p FROM Professeur p " +
            "LEFT JOIN FETCH p.affectations a " +
            "LEFT JOIN a.module m " +
            "GROUP BY p.id " +
            "HAVING (p.grade = 'MA' AND COALESCE(SUM(m.nombre_heures), 0) <= 90) " +
            "OR (p.grade = 'PHD' AND COALESCE(SUM(m.nombre_heures), 0) <= 150)"
    )
    List<Professeur> findAvailableProfesseurs();
}
