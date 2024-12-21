package upf.pjt.cahier_de_textes.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import upf.pjt.cahier_de_textes.dao.dtos.CustomUserDetails;
import upf.pjt.cahier_de_textes.dao.entities.User;

public class AuthUtils {
    private AuthUtils() {
        // Private constructor to prevent instantiation
    }

    public static User getAuthenticatedUser() throws IllegalStateException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                return userDetails.getUser();
            }
            throw new IllegalStateException("User is not authenticated");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch the authenticated user: " + e.getMessage(), e);
        }
    }
}
