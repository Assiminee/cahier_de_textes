package upf.pjt.cahier_de_textes.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.*;
import upf.pjt.cahier_de_textes.entities.Affectation;
import upf.pjt.cahier_de_textes.entities.Professeur;

import java.util.UUID;

public interface AffectationRepository extends  JpaRepository<Affectation, UUID> {
    @Transactional
    void deleteAllByProfId(UUID profId);
}
