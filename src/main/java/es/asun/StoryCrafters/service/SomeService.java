package es.asun.StoryCrafters.service;

import org.springframework.stereotype.Service;

@Service
public class SomeService {

    private final EmailService emailService;

    public SomeService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void enviarCorreoEjemplo() {
        String destinatario = "ansuncg@gmail.com";
        String asunto = "Asunto del correo";
        String mensaje = "Este es un ejemplo de mensaje.";

        emailService.sendEmail(destinatario, asunto, mensaje);
    }
}
