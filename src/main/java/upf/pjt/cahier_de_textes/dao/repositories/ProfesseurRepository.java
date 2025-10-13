package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT COALESCE(SUM(m.nombre_heures), 0) " +
            "FROM Affectation a " +
            "JOIN a.module m " +
            "WHERE a.prof = :prof")
    Long getTotalHoursTaught(@Param("prof") Professeur prof);

    @Query("SELECT p FROM Professeur p WHERE " +
            "(:nomComplet = '' OR LOWER(CONCAT(p.nom, ' ', p.prenom)) LIKE LOWER(CONCAT('%', :nomComplet, '%'))) " +
            "AND (:email = '' OR LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:grade IS NULL OR p.grade = :grade)")

    Page<Professeur> findFilteredProfessors(
            @Param("nomComplet") String nomComplet,
            @Param("email") String email,
            @Param("grade") Grade grade,
            Pageable pageable
    );

    @Query("SELECT p.grade, COUNT(p) FROM Professeur p GROUP BY p.grade")
    List<Object[]> countProfessorsByGrade();
}
