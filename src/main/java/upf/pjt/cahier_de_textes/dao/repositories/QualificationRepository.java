package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Qualification;

import java.util.List;
import java.util.UUID;

@Repository
public interface QualificationRepository extends  JpaRepository<Qualification, UUID> {
    List<Qualification> findAllByProfId(UUID id);
}
