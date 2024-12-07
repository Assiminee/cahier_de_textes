package upf.pjt.cahier_de_textes.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.RoleRepository;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.entities.Role;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.models.CustomUserDetails;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    public void DeleteUserById(UUID id){
        userRepository.deleteById(id);
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
