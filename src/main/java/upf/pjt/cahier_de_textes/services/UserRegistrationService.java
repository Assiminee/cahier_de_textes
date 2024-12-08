package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.RoleRepository;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.dto.UserRegistrationDto;
import upf.pjt.cahier_de_textes.entities.Professeur;
import upf.pjt.cahier_de_textes.entities.Role;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;

import java.util.UUID;

@Service
public class UserRegistrationService {

    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserRegistrationDto dto) {
        // Fetch the role
        RoleEnum selectedRoleEnum = RoleEnum.valueOf(dto.getRole().toUpperCase());
        Role role = roleRepository.findOneByRole(selectedRoleEnum);
        if (role == null) {
            throw new IllegalArgumentException("Role not found for: " + dto.getRole());
        }

        // Create and save the User entity
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

        if (selectedRoleEnum == RoleEnum.ROLE_PROF) {
            // If the role is Professeur, create the Professeur entity
            Professeur professeur = new Professeur();
            professeur.setNom(dto.getNom());
            professeur.setPrenom(dto.getPrenom());
            professeur.setTelephone(dto.getTelephone());
            professeur.setEmail(dto.getEmail());
            professeur.setDateNaissance(dto.getDateNaissance());
            professeur.setAdresse(dto.getAdresse());
            professeur.setGenre(dto.getGenre());
            professeur.setCin(dto.getCin());
            professeur.setPwd(passwordEncoder.encode(dto.getPassword()));
            professeur.setRole(role); // Same role as User
            professeur.setGrade(dto.getGrade());
            professeur.setDateDernierDiplome(dto.getDateDernierDiplome());
            professeur.setDateEmbauche(dto.getDateEmbauche());

            // Save the Professeur directly
            professeurRepository.save(professeur);
            System.out.println("Professeur saved with ID: " + professeur.getId());
        } else {
            // Save the User directly for non-prof roles
            userRepository.save(user);
            System.out.println("User saved with ID: " + user.getId());
        }
    }


}
