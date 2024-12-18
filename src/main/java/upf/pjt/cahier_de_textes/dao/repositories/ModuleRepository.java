package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Module;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleRepository extends  JpaRepository<Module, UUID> {
    List<Module> findByIntituleContainingIgnoreCase(String intitule);
}
