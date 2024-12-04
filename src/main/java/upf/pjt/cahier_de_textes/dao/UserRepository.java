package upf.pjt.cahier_de_textes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.entities.Role;
import upf.pjt.cahier_de_textes.entities.User;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByNom(String nom);
    List<User> findAllByRole(Role role);
    User findByEmailAndPwd(String email, String password);
    User findByEmail(String email);
}
