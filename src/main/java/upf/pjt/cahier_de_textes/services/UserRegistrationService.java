package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.RoleRepository;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.dto.UserRegistrationDto;
import upf.pjt.cahier_de_textes.entities.Role;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;

    @Service
    public class UserRegistrationService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        public void registerUser(UserRegistrationDto dto) {
            // Map DTO to Entity
            User user = new User();
            user.setNom(dto.getNom());
            user.setPrenom(dto.getPrenom());
            user.setTelephone(dto.getTelephone());
            user.setEmail(dto.getEmail());
            user.setDateNaissance(dto.getDateNaissance());
            user.setAdresse(dto.getAdresse());
            user.setGenre(dto.getGenre());
            user.setCin(dto.getCin());

            // Validate Password in the Service
            String password = dto.getPassword();
            if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                throw new IllegalArgumentException("Password must meet security requirements.");
            }

            // Set Encoded Password
            user.setPwd(passwordEncoder.encode(password));

            // Convert String to RoleEnum
            RoleEnum roleEnum;
            try {
                roleEnum = RoleEnum.valueOf(dto.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role specified: " + dto.getRole());
            }

            // Fetch Role by RoleEnum
            Role role = roleRepository.findOneByRole(roleEnum);
            if (role == null) {
                throw new IllegalArgumentException("Role not found for: " + dto.getRole());
            }
            user.setRole(role);

            // Save User
            userRepository.save(user);
        }
    }
