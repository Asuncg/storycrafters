package es.asun.StoryCrafters.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    public void sendEmail(String to, String subject, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);

            System.out.println("Email enviado correctamente a: " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email", e);
        }
    }

    public void enviarInvitacion(String to, String codigoAcceso) {

        String asunto = "Story Craters - Invitación a grupo";
        String mensaje = "¡Hola! Te invito a unirte a nuestro grupo en la plataforma StoryCrafters. El código de acceso es: " + codigoAcceso;

        sendEmail(to, asunto, mensaje);
    }

    public void enviarNotificacion(String to, String feedback, String titulo) {

        String asunto = "Story Craters - Notifiacion sobre estado de tu relato, " + titulo;

        sendEmail(to, asunto, feedback);
    }
}
