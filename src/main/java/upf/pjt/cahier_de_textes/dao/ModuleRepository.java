package upf.pjt.cahier_de_textes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import upf.pjt.cahier_de_textes.entities.Module;

import java.util.UUID;

public interface ModuleRepository extends  JpaRepository<Module, UUID> {}
