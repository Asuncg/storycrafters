package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
/**
 * Clase de utilidad para obtener el usuario autenticado.
 */
public class AuthUtils {

    /**
     * Obtiene el usuario autenticado actualmente.
     *
     * @param userService Servicio de usuario para buscar el usuario por su nombre de usuario.
     * @return El usuario autenticado.
     * @throws UsuarioException si el usuario no se encuentra.
     */
    public static Usuario getAuthUser(UserService userService) throws UsuarioException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Usuario> usuarioOptional = userService.findUserByEmail(username);

        if (usuarioOptional.isEmpty()) {
            throw new UsuarioException("ERROR. Usuario no encontrado");
        }
        return usuarioOptional.get();
    }
}