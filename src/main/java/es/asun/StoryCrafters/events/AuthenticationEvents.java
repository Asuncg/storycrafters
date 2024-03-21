package es.asun.StoryCrafters.events;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class AuthenticationEvents {
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        RequestContextHolder.getRequestAttributes().setAttribute("DataUser", "Se ha autenticado", RequestAttributes.SCOPE_SESSION);
    }

    public  void  onFailure(AbstractAuthenticationEvent failure) {

    }
}
