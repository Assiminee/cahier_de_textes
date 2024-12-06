package upf.pjt.cahier_de_textes.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import upf.pjt.cahier_de_textes.entities.Role;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Map<String, String> ROLE_URL_MAP = Map.of(
            "ROLE_PROF", "/hello?name=professor",
            "ROLE_ADMIN", "/hello?name=admin"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String redirectUrl = ROLE_URL_MAP.get(authority.getAuthority());
            if (redirectUrl != null) {
                response.sendRedirect("/profile");
                return;
            }
        }
        response.sendRedirect("/hello?name=none");
    }
}