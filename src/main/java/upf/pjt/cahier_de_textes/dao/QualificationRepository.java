package upf.pjt.cahier_de_textes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import upf.pjt.cahier_de_textes.entities.Qualification;

import java.util.UUID;

public interface QualificationRepository extends  JpaRepository<Qualification, UUID> {}
