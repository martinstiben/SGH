package com.horarios.SGH.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para envío de correos electrónicos con plantillas HTML.
 * Principio de responsabilidad única (SRP): Solo maneja el envío de emails.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String SYSTEM_NAME = "Sistema de Gestión de Horarios - SGH";
    private static final String SYSTEM_EMAIL = "sgh@university.edu"; // Cambiar por email real

    /**
     * Envía una notificación por email con formato HTML.
     */
    public void sendNotificationEmail(String to, String subject, String title, String message, String roleName)
            throws MessagingException {

        String htmlContent = buildHtmlTemplate(title, message, roleName);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(SYSTEM_EMAIL);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    /**
     * Construye la plantilla HTML para las notificaciones.
     */
    private String buildHtmlTemplate(String title, String message, String roleName) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String htmlTemplate = "<!DOCTYPE html>\n" +
            "<html lang=\"es\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Notificación del Sistema SGH</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
            "            line-height: 1.6;\n" +
            "            color: #333;\n" +
            "            max-width: 600px;\n" +
            "            margin: 0 auto;\n" +
            "            background-color: #f4f4f4;\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "        .container {\n" +
            "            background-color: white;\n" +
            "            border-radius: 10px;\n" +
            "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
            "            overflow: hidden;\n" +
            "        }\n" +
            "        .header {\n" +
            "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
            "            color: white;\n" +
            "            padding: 30px 20px;\n" +
            "            text-align: center;\n" +
            "        }\n" +
            "        .header h1 {\n" +
            "            margin: 0;\n" +
            "            font-size: 24px;\n" +
            "            font-weight: 300;\n" +
            "        }\n" +
            "        .header .subtitle {\n" +
            "            margin: 5px 0 0 0;\n" +
            "            font-size: 14px;\n" +
            "            opacity: 0.9;\n" +
            "        }\n" +
            "        .content {\n" +
            "            padding: 30px 20px;\n" +
            "        }\n" +
            "        .notification-card {\n" +
            "            background-color: #f8f9fa;\n" +
            "            border-left: 4px solid #667eea;\n" +
            "            padding: 20px;\n" +
            "            margin: 20px 0;\n" +
            "            border-radius: 5px;\n" +
            "        }\n" +
            "        .notification-title {\n" +
            "            font-size: 18px;\n" +
            "            font-weight: 600;\n" +
            "            color: #2c3e50;\n" +
            "            margin-bottom: 10px;\n" +
            "        }\n" +
            "        .notification-message {\n" +
            "            font-size: 16px;\n" +
            "            color: #555;\n" +
            "            line-height: 1.6;\n" +
            "        }\n" +
            "        .role-badge {\n" +
            "            display: inline-block;\n" +
            "            background-color: #667eea;\n" +
            "            color: white;\n" +
            "            padding: 5px 12px;\n" +
            "            border-radius: 20px;\n" +
            "            font-size: 12px;\n" +
            "            font-weight: 500;\n" +
            "            text-transform: uppercase;\n" +
            "            letter-spacing: 0.5px;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            background-color: #f8f9fa;\n" +
            "            padding: 20px;\n" +
            "            text-align: center;\n" +
            "            border-top: 1px solid #e9ecef;\n" +
            "        }\n" +
            "        .footer-text {\n" +
            "            color: #6c757d;\n" +
            "            font-size: 14px;\n" +
            "            margin: 0;\n" +
            "        }\n" +
            "        .timestamp {\n" +
            "            color: #adb5bd;\n" +
            "            font-size: 12px;\n" +
            "            margin-top: 10px;\n" +
            "        }\n" +
            "        .logo {\n" +
            "            font-size: 28px;\n" +
            "            font-weight: bold;\n" +
            "            color: white;\n" +
            "            margin-bottom: 10px;\n" +
            "        }\n" +
            "        @media only screen and (max-width: 600px) {\n" +
            "            body {\n" +
            "                padding: 10px;\n" +
            "            }\n" +
            "            .header, .content, .footer {\n" +
            "                padding: 20px 15px;\n" +
            "            }\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"header\">\n" +
            "            <div class=\"logo\">SGH</div>\n" +
            "            <h1>Notificación del Sistema</h1>\n" +
            "            <div class=\"subtitle\">Sistema de Gestión de Horarios</div>\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            <div style=\"text-align: center; margin-bottom: 20px;\">\n" +
            "                <span class=\"role-badge\">" + roleName + "</span>\n" +
            "            </div>\n" +
            "            <div class=\"notification-card\">\n" +
            "                <div class=\"notification-title\">\n" +
            "                    " + title + "\n" +
            "                </div>\n" +
            "                <div class=\"notification-message\">\n" +
            "                    " + message + "\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class=\"timestamp\">\n" +
            "                Enviado el: " + currentDate + "\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"footer\">\n" +
            "            <p class=\"footer-text\">\n" +
            "                Este es un mensaje automático del Sistema de Gestión de Horarios.<br>\n" +
            "                Por favor, no responda a este correo.\n" +
            "            </p>\n" +
            "            <p class=\"footer-text\" style=\"margin-top: 10px;\">\n" +
            "                © 2024 Universidad - Sistema SGH\n" +
            "            </p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";

        return htmlTemplate;
    }

    /**
     * Obtiene el nombre legible del rol.
     */
    public String getRoleDisplayName(String role) {
        return switch (role.toUpperCase()) {
            case "MAESTRO" -> "Profesor";
            case "ESTUDIANTE" -> "Estudiante";
            case "COORDINADOR" -> "Coordinador";
            case "DIRECTOR_DE_AREA" -> "Director de Área";
            default -> role;
        };
    }
}