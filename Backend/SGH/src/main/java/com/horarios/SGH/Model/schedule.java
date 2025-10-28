package com.horarios.SGH.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
public class schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "El curso es obligatorio")
    private courses courseId;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    @NotNull(message = "El profesor es obligatorio")
    private teachers teacherId;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    @NotNull(message = "La materia es obligatoria")
    private subjects subjectId;

    @Column(name = "day", nullable = false, length = 20)
    @NotNull(message = "El día es obligatorio")
    @Size(min = 1, max = 20, message = "El día debe tener entre 1 y 20 caracteres")
    private String day;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIME")
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIME")
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;

    @Column(name = "schedule_name", nullable = false, length = 255)
    @NotNull(message = "El nombre del horario es obligatorio")
    @Size(min = 1, max = 255, message = "El nombre del horario debe tener entre 1 y 255 caracteres")
    private String scheduleName;

    @Column(name = "status", length = 20)
    @Size(max = 20, message = "El estado debe tener máximo 20 caracteres")
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "created_by", length = 100)
    @Size(max = 100, message = "El creador debe tener máximo 100 caracteres")
    private String createdBy;

    @Column(name = "approved_by", length = 100)
    @Size(max = 100, message = "El aprobador debe tener máximo 100 caracteres")
    private String approvedBy;

    @Column(name = "approval_comments", columnDefinition = "TEXT")
    private String approvalComments;

    // Constructor vacío
    public schedule() {}

    // Constructor con parámetros principales
    public schedule(Integer id, courses courseId, teachers teacherId, subjects subjectId, String day, LocalTime startTime, LocalTime endTime, String scheduleName) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleName = scheduleName;
    }

    // Getters y setters generados por Lombok (@Data)
}