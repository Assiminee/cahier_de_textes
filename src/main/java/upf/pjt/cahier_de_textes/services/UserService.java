package upf.pjt.cahier_de_textes.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.repositories.*;
import upf.pjt.cahier_de_textes.dao.dtos.UserDTO;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProfesseurRepository professorRepository;
    private final ModuleRepository moduleRepository;
    private final FiliereRepository filiereRepository;

    @Getter
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            FiliereRepository filiereRepository,
            ProfesseurRepository professorRepository,
            ModuleRepository moduleRepository,
            PasswordEncoder encoder
    ) {
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
        this.moduleRepository = moduleRepository;
        this.filiereRepository = filiereRepository;
        this.encoder = encoder;
    }

    public boolean hasUniqueAttributes(UUID id, UserDTO user, RedirectAttributes redirectAttributes) {

        boolean emailExists = userRepository.existsByIdIsNotAndEmail(id, user.getEmail());
        boolean cinExists = userRepository.existsByIdIsNotAndCin(id, user.getCin());
        boolean telephoneExists = userRepository.existsByIdIsNotAndTelephone(id, user.getTelephone());
        boolean hasDuplicateData = emailExists || cinExists || telephoneExists;

        redirectAttributes.addFlashAttribute("error", hasDuplicateData);
        if (emailExists)
            redirectAttributes.addFlashAttribute("email", user.getEmail());

        if (telephoneExists)
            redirectAttributes.addFlashAttribute("telephone", user.getTelephone());

        if (cinExists)
            redirectAttributes.addFlashAttribute("cin", user.getCin());

        if (!hasDuplicateData)
            redirectAttributes.addFlashAttribute("success", true);

        return !hasDuplicateData;
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null)
            throw new IllegalArgumentException("User not found with ID: " + userId);

        if (user.getRole().getAuthority().equals("ROLE_PROF")) {
            Professeur prof = professorRepository.findById(userId).orElse(null);
            if (prof == null)
                throw new IllegalArgumentException("Professeur not found with ID: " + userId);

            for (Module module : prof.getModules()) {
                System.out.println("Nullifying responsable for module: " + module.getId());
                module.setResponsable(null);
                moduleRepository.save(module);
            }
            prof.setModules(null);

            if (prof.getFiliere() != null) {
                System.out.println("Nullifying coordinateur for filiere: " + prof.getFiliere().getId());
                prof.getFiliere().setCoordinateur(null);
                filiereRepository.save(prof.getFiliere());
                prof.setFiliere(null);
            }

            professorRepository.delete(prof);
        }

        userRepository.delete(user);
    }

    public boolean correctPassword(String incomingPassword, String currentPassword){
        return encoder.matches(incomingPassword, currentPassword);
    }

    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails))
            return null;

        User loggedUser = userDetails.getUser();

        if (loggedUser == null)
            return null;

        String role = loggedUser.getRole().getAuthority();

        if (!(role.equals("ROLE_ADMIN") || role.equals("ROLE_SS")))
            return null;

        return loggedUser;
    }
}
