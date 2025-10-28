package com.horarios.SGH.Controller;

import com.horarios.SGH.DTO.responseDTO;
import com.horarios.SGH.Service.ScheduleApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules/approval")
@Tag(name = "Aprobación de Horarios", description = "Gestión del workflow de aprobación de horarios")
public class ScheduleApprovalController {

    private final ScheduleApprovalService approvalService;

    public ScheduleApprovalController(ScheduleApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping("/approve/{scheduleId}")
    @PreAuthorize("hasAuthority('PERMISSION_GENERATE_SCHEDULES')")
    @Operation(
        summary = "Aprobar horario",
        description = "Aprueba un horario creado por un director de área. Solo coordinadores pueden aprobar."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horario aprobado exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Solo coordinadores"),
        @ApiResponse(responseCode = "404", description = "Horario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Horario ya procesado")
    })
    public ResponseEntity<responseDTO> approveSchedule(
            @Parameter(description = "ID del horario a aprobar", example = "1")
            @PathVariable Integer scheduleId,
            Authentication auth) {
        try {
            approvalService.approveSchedule(scheduleId, auth.getName());
            return ResponseEntity.ok(new responseDTO("OK", "Horario aprobado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new responseDTO("ERROR", e.getMessage()));
        }
    }

    @PostMapping("/reject/{scheduleId}")
    @PreAuthorize("hasAuthority('PERMISSION_GENERATE_SCHEDULES')")
    @Operation(
        summary = "Rechazar horario con comentarios",
        description = "Rechaza un horario creado por un director de área con comentarios. El horario vuelve a estado DRAFT para que el director pueda editarlo. Solo coordinadores pueden rechazar."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horario rechazado exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Solo coordinadores"),
        @ApiResponse(responseCode = "404", description = "Horario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Horario ya procesado")
    })
    public ResponseEntity<responseDTO> rejectSchedule(
            @Parameter(description = "ID del horario a rechazar", example = "1")
            @PathVariable Integer scheduleId,
            @Parameter(description = "Comentarios del coordinador sobre el rechazo", example = "El horario se cruza con otra asignatura")
            @RequestParam String comments,
            Authentication auth) {
        try {
            approvalService.rejectSchedule(scheduleId, auth.getName(), comments);
            return ResponseEntity.ok(new responseDTO("OK", "Horario rechazado exitosamente. El director puede editarlo y reenviarlo."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new responseDTO("ERROR", e.getMessage()));
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('PERMISSION_GENERATE_SCHEDULES')")
    @Operation(
        summary = "Obtener horarios pendientes",
        description = "Obtiene todos los horarios pendientes de aprobación. Solo coordinadores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horarios obtenidos exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Solo coordinadores")
    })
    public ResponseEntity<?> getPendingSchedules(Authentication auth) {
        try {
            var schedules = approvalService.getPendingSchedules(auth.getName());
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new responseDTO("ERROR", e.getMessage()));
        }
    }

    @GetMapping("/my-schedules")
    @PreAuthorize("hasAuthority('PERMISSION_CREATE_SCHEDULES')")
    @Operation(
        summary = "Obtener mis horarios creados",
        description = "Obtiene los horarios creados por el director de área actual."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horarios obtenidos exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<?> getMySchedules(Authentication auth) {
        try {
            var schedules = approvalService.getSchedulesByDirector(auth.getName(), auth.getName());
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new responseDTO("ERROR", e.getMessage()));
        }
    }
}