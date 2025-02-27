package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Objectif;
import java.util.UUID;

@Repository
public interface ObjectifRepository extends JpaRepository<Objectif, UUID> {
}
