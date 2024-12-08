package upf.pjt.cahier_de_textes.services;
import java.util.logging.Logger;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.*;
import upf.pjt.cahier_de_textes.entities.Affectation;
import upf.pjt.cahier_de_textes.entities.Professeur;
import upf.pjt.cahier_de_textes.entities.Role;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.models.CustomUserDetails;

import java.io.Console;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private  ProfesseurRepository professorRepository;;
    private  AffectationRepository affectationRepository;
    private  QualificationRepository qualificationRepository;
    private  ModuleRepository moduleRepository;
    private  FiliereRepository filiereRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            ProfesseurRepository professorRepository,
            AffectationRepository affectationRepository,
            QualificationRepository qualificationRepository,
            ModuleRepository moduleRepository,
            FiliereRepository filiereRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.professorRepository = professorRepository;
        this.affectationRepository = affectationRepository;
        this.qualificationRepository = qualificationRepository;
        this.moduleRepository = moduleRepository;
        this.filiereRepository = filiereRepository;
    }

    @Transactional
    public void saveAdminAndUser() {
        // Step 1: Create and save the Admin Role if it doesn't exist
        Role adminRole = roleRepository.findOneByRole(RoleEnum.ROLE_ADMIN);
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setRole(RoleEnum.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        // Step 2: Create Rania as Admin
        User adminUser = userRepository.findByEmail("rania.bouabid@example.com");
        if (adminUser == null) {
            adminUser = createAdminUser(adminRole);
            userRepository.save(adminUser);
        }


    }

    private User createAdminUser(Role adminRole) {
        User admin = new User();
        admin.setNom("Bouabid");
        admin.setPrenom("Rania");
        admin.setEmail("rania.bouabid@example.com");
        admin.setTelephone("+212678910111");
        admin.setAdresse("Fès, Maroc");
        admin.setGenre(upf.pjt.cahier_de_textes.entities.enumerations.Genre.F);
        admin.setCin("AB12345");
        admin.setPwd("SecureP@ss123");
        admin.setRole(adminRole);
        admin.setDateNaissance(java.time.LocalDate.of(1997, 1, 1));
        validateEntity(admin);
        return admin;
    }

    private <T> void validateEntity(T entity) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<T> violation : constraintViolations) {
                System.err.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            throw new IllegalArgumentException("Entity validation failed: " + constraintViolations);
        }
        factory.close();
    }




   /* @Autowired
    public UserService(PasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.encoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }*/

    public List<User> getAllUsers() {
        return userRepository.findAll(); // Fetch all users from the database
    }

    @Transactional
    public void deleteUser(UUID userId) {
        System.out.println("Attempting to delete user with ID: " + userId);

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user instanceof Professeur) {
                Professeur professeur = (Professeur) user;

                System.out.println("Deleting professor associations...");
                // Delete associated Filiere
               /* if (professeur.getFiliere() != null) {
                    System.out.println("Deleting filiere for professor ID: " + professeur.getId());
                    filiereRepository.delete(professeur.getFiliere());
                }*/

                // Delete associated Qualifications
                qualificationRepository.deleteAllByProfId(professeur.getId());

                // Delete associated Modules
                moduleRepository.deleteAllByResponsableId(professeur.getId());
                // Delete associated Affectations
                affectationRepository.deleteAllByProfId(professeur.getId());

                // Delete the professor record
                System.out.println("Deleting professor record...");
                professorRepository.delete(professeur);
            }

            // Finally, delete the user record
            System.out.println("Deleting user record...");
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        System.out.println("UserServiceDetails");
        if (user == null)
            throw new UsernameNotFoundException(username);

        user.setPwd(passwordEncoder.encode(user.getPwd()));

        System.out.println(user);
        return new CustomUserDetails(user);
    }
}
