package upf.pjt.cahier_de_textes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.UserRepository;
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
}
