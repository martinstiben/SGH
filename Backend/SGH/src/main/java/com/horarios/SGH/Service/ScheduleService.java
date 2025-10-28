package com.horarios.SGH.Service;

import com.horarios.SGH.DTO.ScheduleDTO;
import com.horarios.SGH.Model.Days;
import com.horarios.SGH.Model.TeacherAvailability;
import com.horarios.SGH.Model.schedule;
import com.horarios.SGH.Model.courses;
import com.horarios.SGH.Model.subjects;
import com.horarios.SGH.Model.teachers;
import com.horarios.SGH.Model.TeacherSubject;
import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.IScheduleRepository;
import com.horarios.SGH.Repository.ITeacherAvailabilityRepository;
import com.horarios.SGH.Repository.Icourses;
import com.horarios.SGH.Repository.Iteachers;
import com.horarios.SGH.Repository.Isubjects;
import com.horarios.SGH.Repository.TeacherSubjectRepository;
import com.horarios.SGH.Repository.Iusers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final IScheduleRepository scheduleRepo;
    private final ITeacherAvailabilityRepository availabilityRepo;
    private final Icourses courseRepo;
    private final Iteachers teacherRepo;
    private final Isubjects subjectRepo;
    private final TeacherSubjectRepository teacherSubjectRepo;
    private final Iusers userRepository;
    private final PermissionService permissionService;

    private boolean isTeacherAvailable(Integer teacherId, String day, LocalTime start, LocalTime end) {
        try {
            Days dayEnum = Days.valueOf(day);
            List<TeacherAvailability> disponibilidad = availabilityRepo.findByTeacher_IdAndDay(teacherId, dayEnum);
            return disponibilidad.stream().anyMatch(d -> {
                // Verificar si el horario solicitado está cubierto por AM o PM
                boolean coveredByAM = d.getAmStart() != null && d.getAmEnd() != null &&
                        !start.isBefore(d.getAmStart()) && !end.isAfter(d.getAmEnd());
                boolean coveredByPM = d.getPmStart() != null && d.getPmEnd() != null &&
                        !start.isBefore(d.getPmStart()) && !end.isAfter(d.getPmEnd());
                return coveredByAM || coveredByPM;
            });
        } catch (IllegalArgumentException e) {
            // Día no válido (ej. Sábado o Domingo)
            return false;
        }
    }

    @Transactional
    public List<ScheduleDTO> createSchedule(List<ScheduleDTO> assignments, String executedBy) {
        // Verificar permisos
        if (!permissionService.hasPermissionByEmail(executedBy, "CREATE_SCHEDULES")) {
            throw new SecurityException("No tiene permisos para crear horarios");
        }

        users user = userRepository.findByUserName(executedBy).orElse(null);
        if (user == null) {
            throw new SecurityException("Usuario no encontrado");
        }

        List<schedule> entities = new ArrayList<>();

        for (ScheduleDTO dto : assignments) {
            // Validar que el usuario tenga acceso al área del curso
            courses course = courseRepo.findById(dto.getCourseId()).orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            if ("DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
                if (!permissionService.canAccessArea(executedBy, course.getArea())) {
                    throw new SecurityException("No tiene acceso para crear horarios en el área: " + course.getArea());
                }
            }

            // Validar que el usuario tenga acceso al área del profesor
            teachers teacher = teacherRepo.findById(dto.getTeacherId()).orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
            if ("DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
                if (!permissionService.canAccessArea(executedBy, teacher.getArea())) {
                    throw new SecurityException("No tiene acceso para asignar profesores del área: " + teacher.getArea());
                }
            }

            subjects subject = subjectRepo.findById(dto.getSubjectId()).orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            // VALIDACIÓN: Un profesor solo puede estar asociado a UNA materia
            List<TeacherSubject> teacherAssociations = teacherSubjectRepo.findByTeacher_Id(teacher.getId());
            if (teacherAssociations.size() > 1) {
                throw new RuntimeException("El profesor " + teacher.getTeacherName() +
                    " está asociado a múltiples materias. Cada profesor debe estar asociado únicamente a una materia.");
            }

            // Validar que el profesor esté vinculado específicamente a esta materia
            boolean isLinkedToSubject = teacherSubjectRepo.existsByTeacher_IdAndSubject_Id(teacher.getId(), subject.getId());
            if (!isLinkedToSubject) {
                throw new RuntimeException("El profesor " + teacher.getTeacherName() +
                    " no está vinculado a la materia " + subject.getSubjectName() +
                    ". Debe existir una relación TeacherSubject entre ellos.");
            }

            if (!isTeacherAvailable(teacher.getId(), dto.getDay(), dto.getStartTimeAsLocalTime(), dto.getEndTimeAsLocalTime())) {
                throw new RuntimeException("El profesor " + teacher.getTeacherName() + " no está disponible el " + dto.getDay());
            }

            schedule s = toEntity(dto);
            s.setCourseId(course);
            s.setTeacherId(teacher);
            s.setSubjectId(subject);

            // Estado inicial según el rol
            if ("DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
                s.setStatus("PENDING"); // Horarios creados por director empiezan como pendientes
            } else {
                s.setStatus("APPROVED"); // Coordinadores crean horarios ya aprobados
            }

            s.setCreatedBy(executedBy);
            entities.add(s);
        }

        scheduleRepo.saveAll(entities);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getByName(String scheduleName) {
        return scheduleRepo.findByScheduleName(scheduleName)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getByCourse(Integer courseId, String userEmail) {
        // Verificar permisos y filtrar por área si es necesario
        if (!permissionService.hasPermissionByEmail(userEmail, "VIEW_SCHEDULES")) {
            throw new SecurityException("No tiene permisos para ver horarios");
        }

        users user = userRepository.findByUserName(userEmail).orElse(null);
        if (user == null) {
            throw new SecurityException("Usuario no encontrado");
        }

        // Si es director de área, verificar que el curso pertenezca a su área
        if ("DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
            courses course = courseRepo.findById(courseId).orElse(null);
            if (course == null) {
                throw new RuntimeException("Curso no encontrado");
            }

            if (!permissionService.canAccessArea(userEmail, course.getArea())) {
                throw new SecurityException("No tiene acceso a cursos de esta área");
            }
        }

        return scheduleRepo.findByCourseId(courseId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getByTeacher(Integer teacherId, String userEmail) {
        // Verificar permisos y filtrar por área si es necesario
        if (!permissionService.hasPermissionByEmail(userEmail, "VIEW_SCHEDULES")) {
            throw new SecurityException("No tiene permisos para ver horarios");
        }

        users user = userRepository.findByUserName(userEmail).orElse(null);
        if (user == null) {
            throw new SecurityException("Usuario no encontrado");
        }

        // Si es director de área, verificar que el profesor pertenezca a su área
        if ("DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
            teachers teacher = teacherRepo.findById(teacherId).orElse(null);
            if (teacher == null) {
                throw new RuntimeException("Profesor no encontrado");
            }

            if (!permissionService.canAccessArea(userEmail, teacher.getArea())) {
                throw new SecurityException("No tiene acceso a profesores de esta área");
            }
        }

        return scheduleRepo.findByTeacherId(teacherId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getByCourse(Integer courseId) {
        return scheduleRepo.findByCourseId(courseId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getByTeacher(Integer teacherId) {
        return scheduleRepo.findByTeacherId(teacherId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getAll() {
        return scheduleRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ScheduleDTO updateSchedule(Integer id, ScheduleDTO dto, String executedBy) {
        // Verificar permisos
        if (!permissionService.hasPermissionByEmail(executedBy, "UPDATE_SCHEDULES")) {
            throw new SecurityException("No tiene permisos para actualizar horarios");
        }

        users user = userRepository.findByUserName(executedBy).orElse(null);
        if (user == null) {
            throw new SecurityException("Usuario no encontrado");
        }

        System.out.println("Updating schedule with id: " + id + ", dto: " + dto);
        schedule existing = scheduleRepo.findById(id).orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        // Validar que el director solo pueda editar sus propios horarios en estado DRAFT o PENDING
        if ("DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
            if (!executedBy.equals(existing.getCreatedBy())) {
                throw new SecurityException("Solo puede editar horarios creados por usted");
            }

            if (!"DRAFT".equals(existing.getStatus()) && !"PENDING".equals(existing.getStatus())) {
                throw new SecurityException("Solo puede editar horarios en estado DRAFT o PENDING");
            }

            // Validar acceso por área
            courses course = courseRepo.findById(dto.getCourseId()).orElse(null);
            if (course != null && !permissionService.canAccessArea(executedBy, course.getArea())) {
                throw new SecurityException("No tiene acceso para actualizar horarios en el área: " + course.getArea());
            }

            teachers teacher = teacherRepo.findById(dto.getTeacherId()).orElse(null);
            if (teacher != null && !permissionService.canAccessArea(executedBy, teacher.getArea())) {
                throw new SecurityException("No tiene acceso para asignar profesores del área: " + teacher.getArea());
            }

            // Si estaba rechazado y ahora se actualiza, vuelve a PENDING
            if ("DRAFT".equals(existing.getStatus())) {
                existing.setStatus("PENDING");
                existing.setApprovalComments(null); // Limpiar comentarios anteriores
            }
        }

        courses course = courseRepo.findById(dto.getCourseId()).orElseThrow();

        teachers teacher;
        subjects subject;

        // VALIDACIÓN: Si se especifica teacherId, subjectId es obligatorio y viceversa
        if (dto.getTeacherId() != null && dto.getSubjectId() == null) {
            throw new RuntimeException("Si especificas teacherId, también debes especificar subjectId.");
        }
        if (dto.getSubjectId() != null && dto.getTeacherId() == null) {
            throw new RuntimeException("Si especificas subjectId, también debes especificar teacherId.");
        }

        // Si se especifica teacherId y subjectId, usar esos valores
        if (dto.getTeacherId() != null && dto.getSubjectId() != null) {
            teacher = teacherRepo.findById(dto.getTeacherId()).orElseThrow();
            subject = subjectRepo.findById(dto.getSubjectId()).orElseThrow();

            // VALIDACIÓN: Un profesor solo puede estar asociado a UNA materia
            List<TeacherSubject> teacherAssociations = teacherSubjectRepo.findByTeacher_Id(teacher.getId());
            if (teacherAssociations.size() > 1) {
                throw new RuntimeException("El profesor " + teacher.getTeacherName() +
                    " está asociado a múltiples materias. Cada profesor debe estar asociado únicamente a una materia.");
            }

            // Validar que el profesor esté vinculado específicamente a esta materia
            boolean isLinkedToSubject = teacherSubjectRepo.existsByTeacher_IdAndSubject_Id(teacher.getId(), subject.getId());
            if (!isLinkedToSubject) {
                throw new RuntimeException("El profesor " + teacher.getTeacherName() +
                    " no está vinculado a la materia " + subject.getSubjectName() +
                    ". Debe existir una relación TeacherSubject entre ellos.");
            }
        } else {
            // Si no se especifica profesor/materia, es un error
            throw new RuntimeException("Debes especificar tanto teacherId como subjectId para actualizar el horario.");
        }

        if (!isTeacherAvailable(teacher.getId(), dto.getDay(), dto.getStartTimeAsLocalTime(), dto.getEndTimeAsLocalTime())) {
            throw new RuntimeException("El profesor " + teacher.getTeacherName() + " no está disponible el " + dto.getDay());
        }

        // Actualizar la entidad existente
        existing.setCourseId(course);
        existing.setTeacherId(teacher);
        existing.setSubjectId(subject);
        existing.setDay(dto.getDay());
        existing.setStartTime(dto.getStartTimeAsLocalTime());
        existing.setEndTime(dto.getEndTimeAsLocalTime());
        existing.setScheduleName(dto.getScheduleName());

        schedule saved = scheduleRepo.save(existing);
        return toDTO(saved);
    }

    @Transactional
    public void deleteSchedule(Integer id, String executedBy) {
        // Verificar permisos
        if (!permissionService.hasPermissionByEmail(executedBy, "DELETE_SCHEDULES")) {
            throw new SecurityException("No tiene permisos para eliminar horarios");
        }

        users user = userRepository.findByUserName(executedBy).orElse(null);
        if (user == null) {
            throw new SecurityException("Usuario no encontrado");
        }

        if (!scheduleRepo.existsById(id)) {
            throw new RuntimeException("Horario no encontrado");
        }

        schedule schedule = scheduleRepo.findById(id).orElse(null);
        if (schedule != null && "DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
            // Verificar acceso por área
            if (!permissionService.canAccessArea(executedBy, schedule.getCourseId().getArea())) {
                throw new SecurityException("No tiene acceso para eliminar horarios de esta área");
            }
        }

        scheduleRepo.deleteById(id);
    }

    @Transactional
    public void deleteByDay(String day) {
        scheduleRepo.deleteByDay(day);
    }

    private schedule toEntity(ScheduleDTO dto) {
        schedule s = new schedule();
        s.setId(dto.getId());
        s.setCourseId(courseRepo.findById(dto.getCourseId()).orElseThrow());
        s.setTeacherId(teacherRepo.findById(dto.getTeacherId()).orElseThrow());
        s.setSubjectId(subjectRepo.findById(dto.getSubjectId()).orElseThrow());
        s.setDay(dto.getDay());
        s.setStartTime(dto.getStartTimeAsLocalTime());
        s.setEndTime(dto.getEndTimeAsLocalTime());
        s.setScheduleName(dto.getScheduleName());
        return s;
    }

    private ScheduleDTO toDTO(schedule s) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(s.getId());
        dto.setCourseId(s.getCourseId().getId());
        dto.setTeacherId(s.getTeacherId().getId());
        dto.setSubjectId(s.getSubjectId().getId());
        dto.setDay(s.getDay());
        dto.setStartTimeFromLocalTime(s.getStartTime());
        dto.setEndTimeFromLocalTime(s.getEndTime());
        dto.setScheduleName(s.getScheduleName());
        dto.setTeacherName(s.getTeacherId().getTeacherName());
        dto.setSubjectName(s.getSubjectId().getSubjectName());

        return dto;
    }
}