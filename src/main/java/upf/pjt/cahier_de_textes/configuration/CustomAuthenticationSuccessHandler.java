package upf.pjt.cahier_de_textes.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.RoleEnum;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String redirectUrl = String.valueOf(RoleEnum.valueOf(authority.getAuthority()));

            if (redirectUrl != null) {
                response.sendRedirect("/acceuil");
                return;
            }
        }
    }
}