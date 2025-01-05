package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Cahier;
import java.util.UUID;

@Repository
public interface CahierRepository extends JpaRepository<Cahier, UUID> {
}
