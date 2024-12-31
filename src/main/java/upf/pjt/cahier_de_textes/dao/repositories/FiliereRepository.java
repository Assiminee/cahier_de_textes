package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Diplome;

import java.util.UUID;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, UUID> {
    @Query("SELECT f FROM Filiere f " +
            "LEFT JOIN f.coordinateur c " +
            "WHERE f.intitule LIKE CONCAT('%', :intitule, '%') " +
            "AND ((:coordinateur = '' AND c IS NULL) OR CONCAT(c.nom, ' ', c.prenom) LIKE CONCAT('%', :coordinateur, '%')) " +
            "AND (:diplome IS NULL OR f.diplome = :diplome) " +
            "AND (:reconnue IS NULL OR ((:reconnue = false AND (f.dateExpiration IS NULL OR f.dateExpiration <= CURRENT_DATE)) " +
            "OR (:reconnue = true AND f.dateExpiration > CURRENT_DATE)))"
    )
    Page<Filiere> getFilieres(
            @Param("intitule") String intitule,
            @Param("coordinateur") String coordinateur,
            @Param("diplome") Diplome diplome,
            @Param("reconnue") Boolean reconnue,
            Pageable pageable
    );

    @Query("SELECT f FROM Filiere f " +
            "LEFT JOIN FETCH f.affectations a " +
            "LEFT JOIN f.coordinateur c " +
            "WHERE f.intitule LIKE CONCAT('%', :intitule, '%') " +
            "AND ((:coordinateur = '' AND c IS NULL) OR CONCAT(c.nom, ' ', c.prenom) LIKE CONCAT('%', :coordinateur, '%')) " +
            "AND (:diplome IS NULL OR f.diplome = :diplome) " +
            "AND (:reconnue IS NULL OR ((:reconnue = false AND (f.dateExpiration IS NULL OR f.dateExpiration <= CURRENT_DATE)) " +
            "OR (:reconnue = true AND f.dateExpiration > CURRENT_DATE)))"
    )
    Page<Filiere> getFilieresWithAffectations(
            @Param("intitule") String intitule,
            @Param("coordinateur") String coordinateur,
            @Param("diplome") Diplome diplome,
            @Param("reconnue") Boolean reconnue,
            Pageable pageable
    );

    Boolean existsByIntitule(String intitule);
    Boolean existsByCoordinateur(Professeur coordinateur);
    Boolean existsByIdIsNotAndIntitule(UUID id, String intitule);
    Boolean existsByIdIsNotAndCoordinateur(UUID id, Professeur coordinateur);
}
