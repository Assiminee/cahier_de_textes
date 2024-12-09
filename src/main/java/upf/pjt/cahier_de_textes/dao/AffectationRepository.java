package upf.pjt.cahier_de_textes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import upf.pjt.cahier_de_textes.dao.entities.Affectation;

import java.util.UUID;

public interface AffectationRepository extends  JpaRepository<Affectation, UUID> {}
