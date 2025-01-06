package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.entities.Qualification;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.RoleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import upf.pjt.cahier_de_textes.dao.dtos.UserRegistrationDto;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;


// package upf.pjt.cahier_de_textes.services;

@Service
public class UserRegistrationService {

    private final ProfesseurRepository professeurRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationService(
            ProfesseurRepository professeurRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.professeurRepository = professeurRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(UserRegistrationDto dto) {

        RoleEnum selectedRoleEnum = RoleEnum.valueOf(dto.getRole().toUpperCase());
        Role role = roleRepository.findOneByRole(selectedRoleEnum);
        if (role == null) {
            throw new IllegalArgumentException("Role not found for: " + dto.getRole());
        }

        User user = new User();
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setTelephone(dto.getTelephone());
        user.setEmail(dto.getEmail());
        user.setDateNaissance(dto.getDateNaissance());
        user.setAdresse(dto.getAdresse());
        user.setGenre(dto.getGenre());
        user.setCin(dto.getCin());
        user.setPwd(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);

            userRepository.save(user);
            System.out.println("User saved with ID: " + user.getId());
        }
    }

