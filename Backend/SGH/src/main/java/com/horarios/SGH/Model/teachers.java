package com.horarios.SGH.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Entity(name="teachers")
@Data
public class teachers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="teacherId")
    private int id;

    @Column(name="teacherName", length = 100, nullable=false)
    @NotNull(message = "El nombre del docente es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre del docente debe tener entre 1 y 100 caracteres")
    private String teacherName;

    @Column(name = "area", length = 100)
    @Size(max = 100, message = "El área debe tener máximo 100 caracteres")
    private String area;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeacherSubject> teacherSubjects;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeacherAvailability> availabilities;

    @OneToMany(mappedBy = "teacherId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<schedule> schedules;

    @OneToMany(mappedBy = "gradeDirector", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<courses> directedCourses;

    @Lob
    @Column(name = "photo_data", columnDefinition = "LONGBLOB")
    private byte[] photoData;

    @Column(name = "photo_content_type", length = 100)
    @Size(max = 100, message = "El tipo de contenido de la foto debe tener máximo 100 caracteres")
    private String photoContentType;

    @Column(name = "photo_file_name", length = 255)
    @Size(max = 255, message = "El nombre del archivo de la foto debe tener máximo 255 caracteres")
    private String photoFileName;

    // Constructor vacío
    public teachers() {}

    // Constructor con parámetros principales
    public teachers(int id, String teacherName) {
        this.id = id;
        this.teacherName = teacherName;
    }

    // Getters y setters generados por Lombok (@Data)
}