package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthUtils {
    public static Usuario getAuthUser(UserService userService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Usuario> usuarioOptional = userService.findUserByEmail(username);

        if (usuarioOptional.isEmpty()) {
            //agregar excepcion
        }
        return usuarioOptional.get();
    }
}