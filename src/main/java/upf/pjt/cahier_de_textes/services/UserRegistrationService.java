package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upf.pjt.cahier_de_textes.dao.repositories.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.repositories.RoleRepository;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import upf.pjt.cahier_de_textes.dao.dtos.UserRegistrationDto;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import upf.pjt.cahier_de_textes.dao.entities.Role;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;


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

        // Common fields for User
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
            // Validate and save professor-specific fields
            Professeur professeur = new Professeur();
            professeur.setNom(user.getNom());
            professeur.setPrenom(user.getPrenom());
            professeur.setTelephone(user.getTelephone());
            professeur.setEmail(user.getEmail());
            professeur.setDateNaissance(user.getDateNaissance());
            professeur.setAdresse(user.getAdresse());
            professeur.setGenre(user.getGenre());
            professeur.setCin(user.getCin());
            professeur.setPwd(user.getPwd());
            professeur.setRole(user.getRole());

            if (dto.getGrade() == null || dto.getDateDernierDiplome() == null || dto.getDateEmbauche() == null) {
                throw new IllegalArgumentException("Professor-specific fields cannot be null.");
            }
            professeur.setGrade(dto.getGrade());
            professeur.setDateDernierDiplome(dto.getDateDernierDiplome());
            professeur.setDateEmbauche(dto.getDateEmbauche());

            professeurRepository.save(professeur);
            System.out.println("Professeur saved with ID: " + professeur.getId());
        } else {
            // Save non-professor user
            userRepository.save(user);
            System.out.println("User saved with ID: " + user.getId());
        }
    }
}