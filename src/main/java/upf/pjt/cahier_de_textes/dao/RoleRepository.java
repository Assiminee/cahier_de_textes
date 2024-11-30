package upf.pjt.cahier_de_textes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import upf.pjt.cahier_de_textes.entities.Professeur;
import upf.pjt.cahier_de_textes.entities.Role;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;

import java.util.UUID;

public interface RoleRepository extends  JpaRepository<Role, Integer> {
    Role findOneByRole(RoleEnum role);
}
