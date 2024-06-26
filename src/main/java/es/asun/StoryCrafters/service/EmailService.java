package es.asun.StoryCrafters.service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar correos electrónicos.
 */
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    /**
     * Envía un correo electrónico.
     * @param to Dirección de correo electrónico del destinatario.
     * @param subject Asunto del correo electrónico.
     * @param htmlContent Contenido HTML del correo electrónico.
     */
    public void sendEmail(String to, String subject, String htmlContent) {
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
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlContent, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email enviado correctamente a: " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email", e);
        }
    }

    /**
     * Envía una invitación a unirse a un grupo.
     * @param to Dirección de correo electrónico del destinatario.
     * @param codigoAcceso Código de acceso al grupo.
     * @param descripcion Descripción del grupo.
     * @param nombreGrupo Nombre del grupo.
     * @param nombreGestor Nombre del gestor del grupo.
     */
    public void enviarInvitacion(String to, String codigoAcceso, String descripcion, String nombreGrupo, String nombreGestor) {
        String asunto = "StoryCrafters - Invitación a grupo";
        String url = "https://storycrafters-production.up.railway.app/";
        String mensajeHtml = "<html>"
                + "<body>"
                + "<div style='text-align: center; padding: 20px; font-family: Arial, sans-serif;'>"
                + "<h2>¡HOLA!</h2>"
                + "<p><strong>" + nombreGestor+ "</strong> Te invita a unirte a su grupo en la plataforma <strong>StoryCrafters</strong>.</p>"
                + "<p><strong>Nombre del grupo:</strong> " + nombreGrupo + "</p>"
                + "<p><strong>Descripción:</strong> " + descripcion + "</p>"
                + "<p><strong>Código de acceso:</strong> " + codigoAcceso + "</p>"
                + "<p>Haz clic en el siguiente enlace para visitar nuestra plataforma:</p>"
                + "<p><a href='" + url + "' style='color: #1693A5;'>StoryCrafters</a></p>"
                + "<p>Esperamos verte pronto.</p>"
                + "<p>Saludos,<br/>El equipo de StoryCrafters</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendEmail(to, asunto, mensajeHtml);
    }
}
