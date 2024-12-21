package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Module;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleRepository extends  JpaRepository<Module, UUID> {
        Page<Module> findByIntituleContainingIgnoreCaseAndResponsable_NomContainingIgnoreCase(String intitule, String responsableNom, Pageable pageable);

        Page<Module> findByIntituleContainingIgnoreCase(String intitule, Pageable pageable);

        Page<Module> findByResponsable_NomContainingIgnoreCase(String responsableNom, Pageable pageable);
    }