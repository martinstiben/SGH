package com.horarios.SGH.Controller;

import com.horarios.SGH.Model.Roles;
import com.horarios.SGH.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de notificaciones por email.
 * Solo accesible para coordinadores y directores de área.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Endpoints para envío de notificaciones por email")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Envía notificación a un rol específico.
     */
    @PostMapping("/send-to-role")
    @PreAuthorize("hasRole('COORDINADOR') or hasRole('DIRECTOR_DE_AREA')")
    @Operation(summary = "Enviar notificación a un rol específico",
               description = "Envía una notificación por email a todos los usuarios de un rol determinado")
    public ResponseEntity<?> sendToRole(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String roleName) {

        try {
            Roles targetRole = new Roles(roleName.toUpperCase());
            notificationService.sendNotificationToRole(title, message, targetRole);

            return ResponseEntity.ok(Map.of(
                "message", "Notificación enviada exitosamente al rol: " + roleName,
                "title", title,
                "role", roleName
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al enviar notificación: " + e.getMessage()
            ));
        }
    }

    /**
     * Envía notificación a múltiples roles.
     */
    @PostMapping("/send-to-roles")
    @PreAuthorize("hasRole('COORDINADOR') or hasRole('DIRECTOR_DE_AREA')")
    @Operation(summary = "Enviar notificación a múltiples roles",
               description = "Envía una notificación por email a usuarios de varios roles")
    public ResponseEntity<?> sendToMultipleRoles(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam List<String> roleNames) {

        try {
            List<Roles> targetRoles = roleNames.stream()
                .map(name -> new Roles(name.toUpperCase()))
                .toList();

            notificationService.sendNotificationToMultipleRoles(title, message, targetRoles);

            return ResponseEntity.ok(Map.of(
                "message", "Notificación enviada exitosamente a los roles: " + String.join(", ", roleNames),
                "title", title,
                "roles", roleNames
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al enviar notificaciones: " + e.getMessage()
            ));
        }
    }

    /**
     * Envía notificación a todos los usuarios del sistema.
     */
    @PostMapping("/send-to-all")
    @PreAuthorize("hasRole('COORDINADOR') or hasRole('DIRECTOR_DE_AREA')")
    @Operation(summary = "Enviar notificación a todos los usuarios",
               description = "Envía una notificación por email a todos los usuarios registrados")
    public ResponseEntity<?> sendToAll(
            @RequestParam String title,
            @RequestParam String message) {

        try {
            notificationService.sendNotificationToAll(title, message);

            return ResponseEntity.ok(Map.of(
                "message", "Notificación enviada exitosamente a todos los usuarios",
                "title", title
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al enviar notificación: " + e.getMessage()
            ));
        }
    }

    /**
     * Reenvía notificaciones fallidas.
     */
    @PostMapping("/retry-failed")
    @PreAuthorize("hasRole('COORDINADOR') or hasRole('DIRECTOR_DE_AREA')")
    @Operation(summary = "Reenviar notificaciones fallidas",
               description = "Reintenta el envío de notificaciones que fallaron anteriormente")
    public ResponseEntity<?> retryFailedNotifications() {
        try {
            notificationService.retryFailedNotifications();

            return ResponseEntity.ok(Map.of(
                "message", "Reintento de envío de notificaciones fallidas iniciado"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al reenviar notificaciones: " + e.getMessage()
            ));
        }
    }

    /**
     * Obtiene estadísticas de notificaciones.
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('COORDINADOR') or hasRole('DIRECTOR_DE_AREA')")
    @Operation(summary = "Obtener estadísticas de notificaciones",
               description = "Devuelve estadísticas sobre el envío de notificaciones")
    public ResponseEntity<?> getNotificationStats() {
        try {
            NotificationService.NotificationStats stats = notificationService.getNotificationStats();

            return ResponseEntity.ok(Map.of(
                "total", stats.getTotal(),
                "sent", stats.getSent(),
                "failed", stats.getFailed(),
                "pending", stats.getPending()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error al obtener estadísticas: " + e.getMessage()
            ));
        }
    }

    /**
     * Lista los roles disponibles para envío de notificaciones.
     */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('COORDINADOR') or hasRole('DIRECTOR_DE_AREA')")
    @Operation(summary = "Obtener roles disponibles",
               description = "Devuelve la lista de roles disponibles para envío de notificaciones")
    public ResponseEntity<?> getAvailableRoles() {
        List<Map<String, String>> roles = List.of(
            Map.of("name", "MAESTRO", "displayName", "Profesor"),
            Map.of("name", "ESTUDIANTE", "displayName", "Estudiante"),
            Map.of("name", "COORDINADOR", "displayName", "Coordinador"),
            Map.of("name", "DIRECTOR_DE_AREA", "displayName", "Director de Área")
        );

        return ResponseEntity.ok(Map.of("roles", roles));
    }
}