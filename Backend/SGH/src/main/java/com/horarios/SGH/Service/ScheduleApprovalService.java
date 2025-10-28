package com.horarios.SGH.Service;

import com.horarios.SGH.Model.schedule;
import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.IScheduleRepository;
import com.horarios.SGH.Repository.Iusers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar el workflow de aprobación de horarios.
 * Los directores de área crean horarios que necesitan aprobación del coordinador.
 */
@Service
public class ScheduleApprovalService {

    private final IScheduleRepository scheduleRepository;
    private final Iusers userRepository;
    private final PermissionService permissionService;

    public ScheduleApprovalService(IScheduleRepository scheduleRepository,
                                 Iusers userRepository,
                                 PermissionService permissionService) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    /**
     * Aprueba un horario creado por un director de área.
     * Solo coordinadores pueden aprobar horarios.
     *
     * @param scheduleId ID del horario a aprobar
     * @param approvedBy Email del usuario que aprueba
     * @throws SecurityException si el usuario no tiene permisos
     * @throws RuntimeException si el horario no existe o ya está aprobado
     */
    @Transactional
    public void approveSchedule(Integer scheduleId, String approvedBy) {
        // Verificar que sea coordinador
        if (!permissionService.hasPermissionByEmail(approvedBy, "GENERATE_SCHEDULES")) {
            throw new SecurityException("Solo coordinadores pueden aprobar horarios");
        }

        schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (!"PENDING".equals(schedule.getStatus())) {
            throw new RuntimeException("El horario ya ha sido procesado");
        }

        schedule.setStatus("APPROVED");
        schedule.setApprovedBy(approvedBy);
        scheduleRepository.save(schedule);
    }

    /**
     * Rechaza un horario creado por un director de área con comentarios.
     * El horario vuelve a estado DRAFT para que el director pueda editarlo.
     * Solo coordinadores pueden rechazar horarios.
     *
     * @param scheduleId ID del horario a rechazar
     * @param approvedBy Email del usuario que rechaza
     * @param comments Comentarios del coordinador sobre el rechazo
     * @throws SecurityException si el usuario no tiene permisos
     * @throws RuntimeException si el horario no existe o ya está procesado
     */
    @Transactional
    public void rejectSchedule(Integer scheduleId, String approvedBy, String comments) {
        // Verificar que sea coordinador
        if (!permissionService.hasPermissionByEmail(approvedBy, "GENERATE_SCHEDULES")) {
            throw new SecurityException("Solo coordinadores pueden rechazar horarios");
        }

        schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (!"PENDING".equals(schedule.getStatus())) {
            throw new RuntimeException("El horario ya ha sido procesado");
        }

        schedule.setStatus("DRAFT"); // Cambia a DRAFT para que el director pueda editar
        schedule.setApprovedBy(approvedBy);
        schedule.setApprovalComments(comments); // Agrega comentarios del coordinador
        scheduleRepository.save(schedule);
    }

    /**
     * Obtiene todos los horarios pendientes de aprobación.
     * Solo coordinadores pueden ver estos horarios.
     *
     * @param userEmail Email del usuario que solicita
     * @return Lista de horarios pendientes
     * @throws SecurityException si el usuario no tiene permisos
     */
    @Transactional(readOnly = true)
    public List<schedule> getPendingSchedules(String userEmail) {
        // Verificar que sea coordinador
        if (!permissionService.hasPermissionByEmail(userEmail, "GENERATE_SCHEDULES")) {
            throw new SecurityException("Solo coordinadores pueden ver horarios pendientes");
        }

        return scheduleRepository.findAll().stream()
                .filter(s -> "PENDING".equals(s.getStatus()))
                .toList();
    }

    /**
     * Obtiene horarios creados por un director de área específico.
     * Solo el director que los creó o coordinadores pueden verlos.
     *
     * @param directorEmail Email del director
     * @param requesterEmail Email del usuario que solicita
     * @return Lista de horarios del director
     * @throws SecurityException si el usuario no tiene permisos
     */
    @Transactional(readOnly = true)
    public List<schedule> getSchedulesByDirector(String directorEmail, String requesterEmail) {
        users requester = userRepository.findByUserName(requesterEmail).orElse(null);
        if (requester == null) {
            throw new SecurityException("Usuario no encontrado");
        }

        // Coordinadores pueden ver todos los horarios
        boolean isCoordinator = "COORDINADOR".equals(requester.getRole().getRoleName());

        // Directores solo pueden ver sus propios horarios
        boolean isOwner = directorEmail.equals(requesterEmail);

        if (!isCoordinator && !isOwner) {
            throw new SecurityException("No tiene permisos para ver estos horarios");
        }

        return scheduleRepository.findAll().stream()
                .filter(s -> directorEmail.equals(s.getCreatedBy()))
                .sorted((s1, s2) -> {
                    // Ordenar por estado: PENDING primero, luego APPROVED, luego DRAFT
                    String status1 = s1.getStatus() != null ? s1.getStatus() : "";
                    String status2 = s2.getStatus() != null ? s2.getStatus() : "";

                    if ("PENDING".equals(status1) && !"PENDING".equals(status2)) return -1;
                    if (!"PENDING".equals(status1) && "PENDING".equals(status2)) return 1;
                    if ("APPROVED".equals(status1) && "DRAFT".equals(status2)) return -1;
                    if ("DRAFT".equals(status1) && "APPROVED".equals(status2)) return 1;

                    return 0; // Mantener orden original si estados son iguales
                })
                .toList();
    }
}