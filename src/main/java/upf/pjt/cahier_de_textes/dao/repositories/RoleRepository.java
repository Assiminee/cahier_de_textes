package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;

@Repository
public interface RoleRepository extends  JpaRepository<Role, Integer> {
    Role findOneByRole(RoleEnum selectedRoleEnum);
    Role findByRole(RoleEnum role);
}
