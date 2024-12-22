package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;

import java.util.UUID;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, UUID> {
    @Query("SELECT f FROM Filiere f " +
            "LEFT JOIN f.coordinateur c " +
            "WHERE f.intitule LIKE CONCAT('%', :intitule, '%') " +
            "AND ((:coordinateur = '' AND c IS NULL) OR CONCAT(c.nom, ' ', c.prenom) LIKE CONCAT('%', :coordinateur, '%')) " +
            "AND (:diplome IS NULL OR f.diplome = :diplome) " +
            "AND (f.dateExpiration <= CURRENT_DATE)"
    )
    Page<Filiere> expiredFilieres(
            @Param("intitule") String intitule,
            @Param("coordinateur") String coordinateur,
            @Param("diplome") Diplome diplome,
            Pageable pageable
    );

    @Query("SELECT f FROM Filiere f " +
            "LEFT JOIN f.coordinateur c " +
            "WHERE f.intitule LIKE CONCAT('%', :intitule, '%') " +
            "AND ((:coordinateur = '' AND c IS NULL) OR CONCAT(c.nom, ' ', c.prenom) LIKE CONCAT('%', :coordinateur, '%')) " +
            "AND (:diplome IS NULL OR f.diplome = :diplome) " +
            "AND f.dateExpiration > CURRENT_DATE"
    )
    Page<Filiere> recognizedFilieres(
            @Param("intitule") String intitule,
            @Param("coordinateur") String coordinateur,
            @Param("diplome") Diplome diplome,
            Pageable pageable
    );

}
