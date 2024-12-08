package upf.pjt.cahier_de_textes.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import upf.pjt.cahier_de_textes.entities.Professeur;
import upf.pjt.cahier_de_textes.entities.Qualification;

import java.util.UUID;

public interface QualificationRepository extends  JpaRepository<Qualification, UUID> {

    @Transactional
    void deleteAllByProfId(UUID profId);
}
