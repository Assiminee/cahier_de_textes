package upf.pjt.cahier_de_textes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import java.util.UUID;

public interface ProfesseurRepository extends  JpaRepository<Professeur, UUID> {}
