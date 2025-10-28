package com.horarios.SGH.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity(name="courses")
@Data
public class courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="courseId")
    private int id;

    @Column(name="courseName", nullable=false, unique=true)
    @NotNull(message = "El nombre del curso es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre del curso debe tener entre 1 y 100 caracteres")
    private String courseName;

    @Column(name = "area", length = 100)
    @Size(max = 100, message = "El área debe tener máximo 100 caracteres")
    private String area;

    @ManyToOne
    @JoinColumn(name = "teacher_subject_id")
    private TeacherSubject teacherSubject;

    @ManyToOne
    @JoinColumn(name = "grade_director_id")
    private teachers gradeDirector;

    @OneToMany(mappedBy = "courseId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<schedule> schedules;

    // Constructor vacío
    public courses() {}

    // Constructor con parámetros principales
    public courses(int id, String courseName) {
        this.id = id;
        this.courseName = courseName;
    }

    // Getters y setters generados por Lombok (@Data)
}