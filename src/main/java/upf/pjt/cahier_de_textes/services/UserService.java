package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.dao.dtos.EditUserDTO;
import upf.pjt.cahier_de_textes.dao.entities.User;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
