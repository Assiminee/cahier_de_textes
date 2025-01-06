package upf.pjt.cahier_de_textes.dao.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.ModeEval;
import java.util.UUID;

@Repository
public interface ModuleRepository extends  JpaRepository<Module, UUID> {
    @Query("SELECT m FROM Module m WHERE " +
            "(:intitule IS NULL OR LOWER(m.intitule) LIKE LOWER(CONCAT('%', :intitule, '%'))) AND " +
            "(:responsable IS NULL OR LOWER(m.responsable.nom) LIKE LOWER(CONCAT('%', :responsable, '%'))) AND " +
            "(:modeEvaluation IS NULL OR m.modeEvaluation = :modeEvaluation) AND " +
            "(:min IS NULL OR m.nombre_heures >= :min) AND " +
            "(:max IS NULL OR m.nombre_heures <= :max)")
    Page<Module> filterModules(@Param("intitule") String intitule,
                               @Param("responsable") String responsable,
                               @Param("modeEvaluation") ModeEval modeEvaluation,
                               @Param("min") Integer min,
                               @Param("max") Integer max,
                               Pageable pageable);

    boolean existsByIntituleIgnoreCase(@NotBlank String intitule);
}