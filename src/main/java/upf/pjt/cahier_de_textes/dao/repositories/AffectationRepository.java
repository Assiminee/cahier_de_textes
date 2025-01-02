package upf.pjt.cahier_de_textes.dao.repositories;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;
import upf.pjt.cahier_de_textes.dao.entities.Filiere;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Jour;

import java.util.UUID;

@Repository
public interface AffectationRepository extends  JpaRepository<Affectation, UUID> {
    // Count the number of affectations that contain a module for a specific
    // (filiere, niveau, semestre) throuple
    // Use case: making sure that no duplicate affectations exist
    Integer countAffectationByFiliereAndModuleAndNiveauAndSemestre(
            Filiere filiere, Module module, int niveau, int semestre
    );

    // Checks if any affectations take place on the same day and start/end time
    // Use case: Making sure that no classes are scheduled at the same time
    Integer countAffectationByFiliereAndNiveauAndSemestreAndJourAndHeureDebutAndHeureFin(
            Filiere filiere, int niveau, int semestre, Jour jour, int heureDebut, int heureFin
    );

    // Counts the number of affectations per professor in a specific
    // (filiere, niveau, semestre) throuple
    // Use case: Making sure that the
    Integer countAffectationByFiliereAndNiveauAndSemestreAndProf(
            Filiere filiere, int niveau, int semestre, Professeur professeur
    );

    @Query("SELECT a FROM Affectation a " +
            "JOIN FETCH a.filiere f " +
            "JOIN a.prof p " +
            "JOIN a.module m " +
            "WHERE f.intitule LIKE CONCAT('%', :filiere, '%') " +
            "AND CONCAT(p.nom, ' ', p.prenom) LIKE CONCAT('%', :prof, '%') " +
            "AND m.intitule LIKE CONCAT('%', :module, '%')"

    )
    Page<Affectation> getAffectations(
            @Param("filiere") String filiere,
            @Param("module") String module,
            @Param("prof") String prof,
            Pageable pageable
    );
}
