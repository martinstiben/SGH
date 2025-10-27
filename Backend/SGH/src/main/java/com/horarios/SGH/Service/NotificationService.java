package com.horarios.SGH.Service;

import com.horarios.SGH.Model.Notification;
import com.horarios.SGH.Model.Roles;
import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.INotificationRepository;
import com.horarios.SGH.Repository.Iusers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de notificaciones por roles.
 * Principio de responsabilidad única (SRP): Gestiona notificaciones y envío de emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final INotificationRepository notificationRepository;
    private final Iusers userRepository;
    private final EmailService emailService;

    /**
     * Envía una notificación a todos los usuarios de un rol específico.
     */
    @Transactional
    public void sendNotificationToRole(String title, String message, Roles targetRole) {
        log.info("Enviando notificación a rol {}: {}", targetRole, title);

        // Obtener todos los usuarios del rol especificado
        List<users> users = userRepository.findByRole(targetRole);

        if (users.isEmpty()) {
            log.warn("No se encontraron usuarios para el rol: {}", targetRole);
            return;
        }

        // Crear y guardar notificaciones para cada usuario
        for (users user : users) {
            Notification notification = new Notification(title, message, convertToRoleEnum(targetRole), user.getPerson().getEmail());
            notificationRepository.save(notification);

            // Enviar email de forma asíncrona
            sendEmailAsync(notification);
        }

        log.info("Notificaciones creadas para {} usuarios del rol {}", users.size(), targetRole);
    }

    /**
     * Envía notificaciones a múltiples roles.
     */
    @Transactional
    public void sendNotificationToMultipleRoles(String title, String message, List<Roles> targetRoles) {
        for (Roles role : targetRoles) {
            sendNotificationToRole(title, message, role);
        }
    }

    /**
     * Envía notificación a todos los usuarios del sistema.
     */
    @Transactional
    public void sendNotificationToAll(String title, String message) {
        log.info("Enviando notificación a todos los usuarios: {}", title);

        List<users> allUsers = userRepository.findAll();

        for (users user : allUsers) {
            Notification notification = new Notification(title, message, convertToRoleEnum(user.getRole()), user.getPerson().getEmail());
            notificationRepository.save(notification);

            // Enviar email de forma asíncrona
            sendEmailAsync(notification);
        }

        log.info("Notificaciones creadas para {} usuarios", allUsers.size());
    }

    /**
     * Reenvía notificaciones fallidas.
     */
    @Transactional
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByIsSent(false);

        log.info("Reintentando envío de {} notificaciones fallidas", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            sendEmailAsync(notification);
        }
    }

    /**
     * Obtiene estadísticas de notificaciones.
     */
    public NotificationStats getNotificationStats() {
        long totalNotifications = notificationRepository.count();
        long sentNotifications = notificationRepository.countByIsSent(true);
        long failedNotifications = notificationRepository.countByIsSent(false);

        return new NotificationStats(totalNotifications, sentNotifications, failedNotifications);
    }

    /**
     * Envía email de forma asíncrona para no bloquear la operación principal.
     */
    @Async
    protected void sendEmailAsync(Notification notification) {
        try {
            String roleDisplayName = emailService.getRoleDisplayName(notification.getTargetRole().name());

            emailService.sendNotificationEmail(
                notification.getRecipientEmail(),
                "SGH - " + notification.getTitle(),
                notification.getTitle(),
                notification.getMessage(),
                roleDisplayName
            );

            // Marcar como enviado
            notification.setIsSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);

            log.info("Email enviado exitosamente a: {}", notification.getRecipientEmail());

        } catch (MessagingException e) {
            log.error("Error enviando email a {}: {}", notification.getRecipientEmail(), e.getMessage());

            // Marcar como fallido y guardar error
            notification.setIsSent(false);
            notification.setErrorMessage(e.getMessage());
            notificationRepository.save(notification);
        }
    }

    /**
     * Clase para estadísticas de notificaciones.
     */
    public static class NotificationStats {
        private final long total;
        private final long sent;
        private final long failed;

        public NotificationStats(long total, long sent, long failed) {
            this.total = total;
            this.sent = sent;
            this.failed = failed;
        }

        public long getTotal() { return total; }
        public long getSent() { return sent; }
        public long getFailed() { return failed; }
        public long getPending() { return total - sent - failed; }
    }

    /**
     * Convierte Roles a Role enum para compatibilidad con Notification.
     */
    private com.horarios.SGH.Model.Role convertToRoleEnum(Roles role) {
        return switch (role.getRoleName().toUpperCase()) {
            case "MAESTRO" -> com.horarios.SGH.Model.Role.MAESTRO;
            case "ESTUDIANTE" -> com.horarios.SGH.Model.Role.ESTUDIANTE;
            case "COORDINADOR" -> com.horarios.SGH.Model.Role.COORDINADOR;
            case "DIRECTOR_DE_AREA" -> com.horarios.SGH.Model.Role.DIRECTOR_DE_AREA;
            default -> com.horarios.SGH.Model.Role.ESTUDIANTE; // default
        };
    }
}