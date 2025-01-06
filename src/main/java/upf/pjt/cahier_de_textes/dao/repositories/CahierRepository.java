package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Cahier;
import java.util.UUID;

@Repository
public interface CahierRepository extends JpaRepository<Cahier, UUID> {
    @Query("SELECT c FROM Cahier c WHERE " +
            "c.archived = true AND " + // Ensures only archived entities are fetched
            "(:filiere = '' OR LOWER(c.filiere) LIKE LOWER(CONCAT('%', :filiere, '%'))) AND " +
            "(:module = '' OR LOWER(c.module) LIKE LOWER(CONCAT('%', :module, '%'))) AND " +
            "(:professeur = '' OR LOWER(c.professeur) LIKE LOWER(CONCAT('%', :professeur, '%'))) AND " +
            "(:niveau IS NULL OR c.niveau = :niveau) AND " +
            "(:semestre IS NULL OR c.semestre = :semestre) AND " +
            "(:annee IS NULL OR FUNCTION('YEAR', c.createdAt) = :annee)")
    Page<Cahier> findArchivedCahiersByFilters(
            @Param("filiere") String filiere,
            @Param("module") String module,
            @Param("professeur") String professeur,
            @Param("niveau") Integer niveau,
            @Param("semestre") Integer semestre,
            @Param("annee") Integer annee,
            Pageable pageable
    );
}
