package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;

import java.util.UUID;

@Repository
public interface ProfesseurRepository extends  JpaRepository<Professeur, UUID> {}
