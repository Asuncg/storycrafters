package es.asun.StoryCrafters.utilidades;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    public static Usuario getAuthUser(UserService userService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findUserByEmail(username);
    }
}