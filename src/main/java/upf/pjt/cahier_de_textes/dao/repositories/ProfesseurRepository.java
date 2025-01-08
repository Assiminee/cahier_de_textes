package upf.pjt.cahier_de_textes.dao.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Grade;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfesseurRepository extends  JpaRepository<Professeur, UUID> {
    List<Professeur> findAllByRole(Role profRole);

    @Query("SELECT p FROM Professeur p WHERE " +
            "(:nomComplet IS NULL OR p.nom LIKE %:nomComplet%) AND " +
            "(:email IS NULL OR p.email LIKE %:email%) AND " +
            "(:grade IS NULL OR p.grade = :grade)")

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

    boolean existsByEmail(@NotBlank @Email String email);


    @Query("SELECT p.grade, COUNT(p) FROM Professeur p GROUP BY p.grade")
    List<Object[]> countProfessorsByGrade();
}
