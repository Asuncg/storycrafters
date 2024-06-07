package es.asun.StoryCrafters.events;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Clase que gestiona eventos relacionados con la autenticación de usuarios.
 */
@Component
public class AuthenticationEvents {

    /**
     * Método que se ejecuta cuando ocurre un evento de autenticación exitosa.
     * Se establece un atributo en el alcance de la sesión para indicar que el usuario se ha autenticado correctamente.
     * @param success Evento de autenticación exitosa.
     */
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        RequestContextHolder.getRequestAttributes().setAttribute("DataUser", "Se ha autenticado", RequestAttributes.SCOPE_SESSION);
    }
}
