package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.entities.Module;
import upf.pjt.cahier_de_textes.dao.repositories.*;
import upf.pjt.cahier_de_textes.dao.dtos.EditUserDTO;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.entities.Professeur;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private ProfesseurRepository professorRepository;;
    private AffectationRepository affectationRepository;
    private QualificationRepository qualificationRepository;
    private  ModuleRepository moduleRepository;
    private  FiliereRepository filiereRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            FiliereRepository filiereRepository,
            RoleRepository roleRepository,
            ProfesseurRepository professorRepository,
            AffectationRepository affectationRepository,
            QualificationRepository qualificationRepository,ModuleRepository moduleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.professorRepository = professorRepository;
        this.qualificationRepository = qualificationRepository;
        this.affectationRepository = affectationRepository;
        this.moduleRepository = moduleRepository;
        this.filiereRepository = filiereRepository;
    }

    private boolean hasDuplicateEmail(User user, String email) {
        if (user.getEmail().equals(email))
            return false;

        return userRepository.existsByEmail(email);
    }

    private boolean hasDuplicateCin(User user, String cin) {
        if (user.getCin().equals(cin))
            return false;

        return userRepository.existsByCin(cin);
    }

    private boolean hasDuplicateTelephone(User user, String telephone) {
        if (user.getTelephone().equals(telephone))
            return false;

        return userRepository.existsByTelephone(telephone);
    }

    public boolean hasUniqueAttributes(User user, EditUserDTO incomingUser, RedirectAttributes redirectAttributes) {

        boolean emailExists = hasDuplicateEmail(user, incomingUser.getEmail());
        boolean cinExists = hasDuplicateCin(user, incomingUser.getCin());
        boolean telephoneExists = hasDuplicateTelephone(user, incomingUser.getTelephone());

        if (emailExists || telephoneExists || cinExists) {
            redirectAttributes.addFlashAttribute("error", true);
            if (emailExists)
                redirectAttributes.addFlashAttribute("email", incomingUser.getEmail());

            if (telephoneExists)
                redirectAttributes.addFlashAttribute("telephone", incomingUser.getTelephone());

            if (cinExists)
                redirectAttributes.addFlashAttribute("cin", incomingUser.getCin());

            return false;
        }
        return true;
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
}
