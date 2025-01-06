package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.repositories.UserRepository;
import upf.pjt.cahier_de_textes.dao.entities.User;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(PasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.encoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null)
            throw new UsernameNotFoundException(username);

        return new CustomUserDetails(user);
    }

    /*
     * Purpose of this method: after the authenticated user's data changes
     * the data in the SecurityContextHolder doesn't get updated.
     */
    public static void updateCustomUserDetails(User updatedUser) {
        // Rebuild the custom user details from the updated user
        CustomUserDetails updatedDetails = new CustomUserDetails(updatedUser);

        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();

        // Create new authentication with updated principal
        UsernamePasswordAuthenticationToken newAuth =
            new UsernamePasswordAuthenticationToken(
                    updatedDetails,
                    oldAuth.getCredentials(),
                    oldAuth.getAuthorities()
            );

        // Update the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
