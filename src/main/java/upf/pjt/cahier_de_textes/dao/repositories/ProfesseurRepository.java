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


    @Query("SELECT COUNT(p) > 0 FROM Professeur p " +
            "WHERE( LOWER(p.email) = LOWER(:email) OR LOWER(p.cin) = LOWER(:cin) OR p.telephone = :telephone) " +
            "AND (:id IS NULL OR p.id <> :id)")
    boolean existsByEmailOrCinOrTelephone(
            @Param("email") String email,
            @Param("cin") String cin,
            @Param("telephone") String telephone,
            @Param("id") UUID id
    );

    boolean existsByIdIsNotAndEmailIgnoreCase(UUID id, String email);
    boolean existsByIdIsNotAndCinIgnoreCase(UUID id, String cin);
    boolean existsByIdIsNotAndTelephone(UUID id, String telephone);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCinIgnoreCase(String email);
    boolean existsByTelephone(String email);

    @Query("SELECT p.grade, COUNT(p) FROM Professeur p GROUP BY p.grade")
    List<Object[]> countProfessorsByGrade();
}
