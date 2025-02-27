package upf.pjt.cahier_de_textes.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.Genre;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    boolean existsByIdIsNotAndEmailIgnoreCase(UUID id, String email);
    boolean existsByIdIsNotAndCinIgnoreCase(UUID id, String cin);
    boolean existsByIdIsNotAndTelephone(UUID id, String telephone);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCinIgnoreCase(String cin);
    boolean existsByTelephone(String telephone);

    @Query("SELECT COUNT(u) FROM User u")
    int countAllUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.genre = :genre")
    int countUsersByGender(Genre genre);

    @Query("SELECT r.role, COUNT(u) FROM User u JOIN u.role r GROUP BY r.role")
    List<Object[]> countUsersByRole();

    @Query("SELECT u FROM User u WHERE " +
            "(:nomComplet = '' OR LOWER(CONCAT(u.nom, ' ', u.prenom)) LIKE LOWER(CONCAT('%', :nomComplet, '%'))) " +
            "AND (:email = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (u.role != :excludedRole)"
    )
    Page<User> searchUsers(
            @Param("nomComplet") String nomComplet,
            @Param("email") String email,
            @Param("role") Role role,
            @Param("excludedRole") Role excludedRole,
            Pageable pageable
    );
}
