package es.asun.StoryCrafters.events;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Clase que gestiona eventos relacionados con el cierre de sesión de usuarios.
 */
@Component
public class LogoutEvent implements ApplicationListener<LogoutSuccessEvent> {

    /**
     * Método que se ejecuta cuando ocurre un evento de cierre de sesión exitoso.
     * Imprime un mensaje indicando que el usuario ha cerrado sesión.
     * @param event Evento de cierre de sesión exitoso.
     */
    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        System.out.printf("El usuario ha cerrado sesion");
    }
}
