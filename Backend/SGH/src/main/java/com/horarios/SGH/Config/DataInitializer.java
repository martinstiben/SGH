package com.horarios.SGH.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.horarios.SGH.Model.users;
import com.horarios.SGH.Model.subjects;
import com.horarios.SGH.Model.teachers;
import com.horarios.SGH.Model.courses;
import com.horarios.SGH.Model.TeacherSubject;
import com.horarios.SGH.Model.TeacherAvailability;
import com.horarios.SGH.Model.Days;
import com.horarios.SGH.Repository.Iusers;
import com.horarios.SGH.Repository.Isubjects;
import com.horarios.SGH.Repository.Iteachers;
import com.horarios.SGH.Repository.Icourses;
import com.horarios.SGH.Repository.TeacherSubjectRepository;
import com.horarios.SGH.Repository.ITeacherAvailabilityRepository;
import com.horarios.SGH.Repository.IPeopleRepository;
import com.horarios.SGH.Repository.IRolesRepository;
import com.horarios.SGH.Repository.IPermissionsRepository;
import com.horarios.SGH.Repository.IPermissionsRolesRepository;
import com.horarios.SGH.Model.People;
import com.horarios.SGH.Model.Roles;
import com.horarios.SGH.Model.Permissions;
import com.horarios.SGH.Model.PermissionsRoles;

import org.springframework.boot.CommandLineRunner;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    @Value("${app.master.username:master}")
    private String masterUsername;

    @Value("${app.master.password:Master$2025!}")
    private String masterPassword;

    @Bean
    public CommandLineRunner seedRoles(IRolesRepository rolesRepo) {
        return args -> {
            if (rolesRepo.count() == 0) {
                rolesRepo.save(new Roles("MAESTRO"));
                rolesRepo.save(new Roles("ESTUDIANTE"));
                rolesRepo.save(new Roles("COORDINADOR"));
                rolesRepo.save(new Roles("DIRECTOR_DE_AREA"));
                System.out.println(">> Roles iniciales creados");
            } else {
                System.out.println(">> Roles ya existen");
            }
        };
    }

    @Bean
    public CommandLineRunner seedPermissions(IPermissionsRepository permissionsRepo) {
        return args -> {
            if (permissionsRepo.count() == 0) {
                // Permisos para horarios
                permissionsRepo.save(new Permissions("VIEW_SCHEDULES", "Ver horarios"));
                permissionsRepo.save(new Permissions("CREATE_SCHEDULES", "Crear horarios manualmente"));
                permissionsRepo.save(new Permissions("UPDATE_SCHEDULES", "Actualizar horarios"));
                permissionsRepo.save(new Permissions("DELETE_SCHEDULES", "Eliminar horarios"));
                permissionsRepo.save(new Permissions("GENERATE_SCHEDULES", "Generar horarios automáticamente"));
                permissionsRepo.save(new Permissions("EXPORT_SCHEDULES", "Exportar horarios"));

                // Permisos para cursos
                permissionsRepo.save(new Permissions("VIEW_COURSES", "Ver cursos"));
                permissionsRepo.save(new Permissions("CREATE_COURSES", "Crear cursos"));
                permissionsRepo.save(new Permissions("UPDATE_COURSES", "Actualizar cursos"));
                permissionsRepo.save(new Permissions("DELETE_COURSES", "Eliminar cursos"));

                // Permisos para materias
                permissionsRepo.save(new Permissions("VIEW_SUBJECTS", "Ver materias"));
                permissionsRepo.save(new Permissions("CREATE_SUBJECTS", "Crear materias"));
                permissionsRepo.save(new Permissions("UPDATE_SUBJECTS", "Actualizar materias"));
                permissionsRepo.save(new Permissions("DELETE_SUBJECTS", "Eliminar materias"));

                // Permisos para profesores
                permissionsRepo.save(new Permissions("VIEW_TEACHERS", "Ver profesores"));
                permissionsRepo.save(new Permissions("CREATE_TEACHERS", "Crear profesores"));
                permissionsRepo.save(new Permissions("UPDATE_TEACHERS", "Actualizar profesores"));
                permissionsRepo.save(new Permissions("DELETE_TEACHERS", "Eliminar profesores"));
                permissionsRepo.save(new Permissions("MANAGE_AVAILABILITY", "Gestionar disponibilidad de profesores"));

                // Permisos para usuarios
                permissionsRepo.save(new Permissions("VIEW_USERS", "Ver usuarios"));
                permissionsRepo.save(new Permissions("CREATE_USERS", "Crear usuarios"));
                permissionsRepo.save(new Permissions("UPDATE_USERS", "Actualizar usuarios"));
                permissionsRepo.save(new Permissions("DELETE_USERS", "Eliminar usuarios"));

                System.out.println(">> Permisos iniciales creados");
            } else {
                System.out.println(">> Permisos ya existen");
            }
        };
    }

    @Bean
    public CommandLineRunner seedPermissionsRoles(IPermissionsRepository permissionsRepo,
                                                IRolesRepository rolesRepo,
                                                IPermissionsRolesRepository permissionsRolesRepo) {
        return args -> {
            if (permissionsRolesRepo.count() == 0) {
                // Obtener roles
                Roles estudiante = rolesRepo.findByRoleName("ESTUDIANTE").orElse(null);
                Roles maestro = rolesRepo.findByRoleName("MAESTRO").orElse(null);
                Roles coordinador = rolesRepo.findByRoleName("COORDINADOR").orElse(null);
                Roles director = rolesRepo.findByRoleName("DIRECTOR_DE_AREA").orElse(null);

                if (estudiante == null || maestro == null || coordinador == null || director == null) {
                    System.out.println(">> Error: Roles no encontrados para asignar permisos");
                    return;
                }

                // Obtener permisos
                Permissions viewSchedules = permissionsRepo.findByPermissionName("VIEW_SCHEDULES").orElse(null);
                Permissions exportSchedules = permissionsRepo.findByPermissionName("EXPORT_SCHEDULES").orElse(null);
                Permissions createSchedules = permissionsRepo.findByPermissionName("CREATE_SCHEDULES").orElse(null);
                Permissions updateSchedules = permissionsRepo.findByPermissionName("UPDATE_SCHEDULES").orElse(null);
                Permissions deleteSchedules = permissionsRepo.findByPermissionName("DELETE_SCHEDULES").orElse(null);
                Permissions generateSchedules = permissionsRepo.findByPermissionName("GENERATE_SCHEDULES").orElse(null);

                Permissions viewCourses = permissionsRepo.findByPermissionName("VIEW_COURSES").orElse(null);
                Permissions createCourses = permissionsRepo.findByPermissionName("CREATE_COURSES").orElse(null);
                Permissions updateCourses = permissionsRepo.findByPermissionName("UPDATE_COURSES").orElse(null);
                Permissions deleteCourses = permissionsRepo.findByPermissionName("DELETE_COURSES").orElse(null);

                Permissions viewSubjects = permissionsRepo.findByPermissionName("VIEW_SUBJECTS").orElse(null);
                Permissions createSubjects = permissionsRepo.findByPermissionName("CREATE_SUBJECTS").orElse(null);
                Permissions updateSubjects = permissionsRepo.findByPermissionName("UPDATE_SUBJECTS").orElse(null);
                Permissions deleteSubjects = permissionsRepo.findByPermissionName("DELETE_SUBJECTS").orElse(null);

                Permissions viewTeachers = permissionsRepo.findByPermissionName("VIEW_TEACHERS").orElse(null);
                Permissions createTeachers = permissionsRepo.findByPermissionName("CREATE_TEACHERS").orElse(null);
                Permissions updateTeachers = permissionsRepo.findByPermissionName("UPDATE_TEACHERS").orElse(null);
                Permissions deleteTeachers = permissionsRepo.findByPermissionName("DELETE_TEACHERS").orElse(null);
                Permissions manageAvailability = permissionsRepo.findByPermissionName("MANAGE_AVAILABILITY").orElse(null);

                Permissions viewUsers = permissionsRepo.findByPermissionName("VIEW_USERS").orElse(null);
                Permissions createUsers = permissionsRepo.findByPermissionName("CREATE_USERS").orElse(null);
                Permissions updateUsers = permissionsRepo.findByPermissionName("UPDATE_USERS").orElse(null);
                Permissions deleteUsers = permissionsRepo.findByPermissionName("DELETE_USERS").orElse(null);

                // Asignar permisos a ESTUDIANTE
                if (viewSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(estudiante, viewSchedules));
                if (exportSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(estudiante, exportSchedules));
                if (viewCourses != null) permissionsRolesRepo.save(new PermissionsRoles(estudiante, viewCourses));

                // Asignar permisos a MAESTRO
                if (viewSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(maestro, viewSchedules));
                if (exportSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(maestro, exportSchedules));
                if (viewCourses != null) permissionsRolesRepo.save(new PermissionsRoles(maestro, viewCourses));
                if (viewSubjects != null) permissionsRolesRepo.save(new PermissionsRoles(maestro, viewSubjects));
                if (viewTeachers != null) permissionsRolesRepo.save(new PermissionsRoles(maestro, viewTeachers));
                if (manageAvailability != null) permissionsRolesRepo.save(new PermissionsRoles(maestro, manageAvailability));

                // Asignar permisos a COORDINADOR (todos los permisos)
                if (viewSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, viewSchedules));
                if (createSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, createSchedules));
                if (updateSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, updateSchedules));
                if (deleteSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, deleteSchedules));
                if (generateSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, generateSchedules));
                if (exportSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, exportSchedules));

                if (viewCourses != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, viewCourses));
                if (createCourses != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, createCourses));
                if (updateCourses != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, updateCourses));
                if (deleteCourses != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, deleteCourses));

                if (viewSubjects != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, viewSubjects));
                if (createSubjects != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, createSubjects));
                if (updateSubjects != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, updateSubjects));
                if (deleteSubjects != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, deleteSubjects));

                if (viewTeachers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, viewTeachers));
                if (createTeachers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, createTeachers));
                if (updateTeachers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, updateTeachers));
                if (deleteTeachers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, deleteTeachers));
                if (manageAvailability != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, manageAvailability));

                if (viewUsers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, viewUsers));
                if (createUsers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, createUsers));
                if (updateUsers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, updateUsers));
                if (deleteUsers != null) permissionsRolesRepo.save(new PermissionsRoles(coordinador, deleteUsers));

                // Asignar permisos a DIRECTOR_DE_AREA (puede crear y actualizar horarios)
                if (viewSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(director, viewSchedules));
                if (createSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(director, createSchedules));
                if (updateSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(director, updateSchedules));
                if (exportSchedules != null) permissionsRolesRepo.save(new PermissionsRoles(director, exportSchedules));
                if (viewCourses != null) permissionsRolesRepo.save(new PermissionsRoles(director, viewCourses));
                if (viewTeachers != null) permissionsRolesRepo.save(new PermissionsRoles(director, viewTeachers));
                if (viewSubjects != null) permissionsRolesRepo.save(new PermissionsRoles(director, viewSubjects));

                System.out.println(">> Permisos asignados a roles");
            } else {
                System.out.println(">> Permisos ya están asignados a roles");
            }
        };
    }

    @Bean
    public CommandLineRunner seedMasterUser(Iusers repo, PasswordEncoder encoder, IPeopleRepository peopleRepo, IRolesRepository rolesRepo) {
        return args -> {
            if (!repo.existsByUserName(masterUsername)) {
                // Verificar si ya existe una persona con este email
                if (peopleRepo.findByEmail(masterUsername).isPresent()) {
                    System.out.println(">> Persona con email master ya existe, saltando creación");
                    return;
                }

                // Crear persona para el usuario master
                People masterPerson = new People("Master User", masterUsername);
                masterPerson = peopleRepo.save(masterPerson);

                // Obtener rol MAESTRO
                Roles maestroRole = rolesRepo.findByRoleName("MAESTRO").orElseThrow(() -> new RuntimeException("Rol MAESTRO no encontrado"));

                users u = new users(masterPerson, maestroRole, encoder.encode(masterPassword));
                u.setArea("Administración"); // Usuario master tiene acceso a todas las áreas
                repo.save(u);
                System.out.println(">> Master creado: " + masterUsername);
            } else {
                System.out.println(">> Master ya existe: " + masterUsername);
            }
            long total = repo.count();
            System.out.println(">> Usuarios totales: " + total + " (sin límite)");
        };
    }

    @Bean
    public CommandLineRunner seedInitialData(Isubjects subjectRepo, Iteachers teacherRepo, Icourses courseRepo, TeacherSubjectRepository teacherSubjectRepo, ITeacherAvailabilityRepository availabilityRepo) {
        return args -> {
            // Crear materias si no existen
            if (subjectRepo.count() == 0) {
                subjects math = new subjects();
                math.setSubjectName("Matemáticas");
                subjectRepo.save(math);

                subjects physics = new subjects();
                physics.setSubjectName("Física");
                subjectRepo.save(physics);

                subjects chemistry = new subjects();
                chemistry.setSubjectName("Química");
                subjectRepo.save(chemistry);

                subjects biology = new subjects();
                biology.setSubjectName("Biología");
                subjectRepo.save(biology);

                System.out.println(">> Materias iniciales creadas");
            }

            // Crear profesores si no existen
            if (teacherRepo.count() == 0) {
                teachers teacher1 = new teachers();
                teacher1.setTeacherName("Juan Pérez");
                teacher1.setArea("Ciencias Básicas"); // Área asignada
                teacher1 = teacherRepo.save(teacher1);

                teachers teacher2 = new teachers();
                teacher2.setTeacherName("María García");
                teacher2.setArea("Ciencias Básicas"); // Área asignada
                teacher2 = teacherRepo.save(teacher2);

                teachers teacher3 = new teachers();
                teacher3.setTeacherName("Carlos López");
                teacher3.setArea("Ciencias Básicas"); // Área asignada
                teacher3 = teacherRepo.save(teacher3);

                // Asignar especializaciones
                subjects math = subjectRepo.findBySubjectName("Matemáticas");
                if (math != null) {
                    TeacherSubject ts1 = new TeacherSubject();
                    ts1.setTeacher(teacher1);
                    ts1.setSubject(math);
                    teacherSubjectRepo.save(ts1);
                }

                subjects physics = subjectRepo.findBySubjectName("Física");
                if (physics != null) {
                    TeacherSubject ts2 = new TeacherSubject();
                    ts2.setTeacher(teacher2);
                    ts2.setSubject(physics);
                    teacherSubjectRepo.save(ts2);
                }

                subjects chemistry = subjectRepo.findBySubjectName("Química");
                if (chemistry != null) {
                    TeacherSubject ts3 = new TeacherSubject();
                    ts3.setTeacher(teacher3);
                    ts3.setSubject(chemistry);
                    teacherSubjectRepo.save(ts3);
                }

                // Crear disponibilidad inicial para profesores
                // Disponibilidad para Juan Pérez (Lunes y Miércoles)
                TeacherAvailability avail1 = new TeacherAvailability();
                avail1.setTeacher(teacher1);
                avail1.setDay(Days.Lunes);
                avail1.setAmStart(LocalTime.of(8, 0));
                avail1.setAmEnd(LocalTime.of(12, 0));
                avail1.setPmStart(LocalTime.of(14, 0));
                avail1.setPmEnd(LocalTime.of(18, 0));
                availabilityRepo.save(avail1);

                TeacherAvailability avail2 = new TeacherAvailability();
                avail2.setTeacher(teacher1);
                avail2.setDay(Days.Miércoles);
                avail2.setAmStart(LocalTime.of(8, 0));
                avail2.setAmEnd(LocalTime.of(12, 0));
                availabilityRepo.save(avail2);

                // Disponibilidad para María García (Martes y Jueves)
                TeacherAvailability avail3 = new TeacherAvailability();
                avail3.setTeacher(teacher2);
                avail3.setDay(Days.Martes);
                avail3.setAmStart(LocalTime.of(9, 0));
                avail3.setAmEnd(LocalTime.of(13, 0));
                availabilityRepo.save(avail3);

                TeacherAvailability avail4 = new TeacherAvailability();
                avail4.setTeacher(teacher2);
                avail4.setDay(Days.Jueves);
                avail4.setAmStart(LocalTime.of(9, 0));
                avail4.setAmEnd(LocalTime.of(13, 0));
                availabilityRepo.save(avail4);

                // Disponibilidad para Carlos López (Viernes)
                TeacherAvailability avail5 = new TeacherAvailability();
                avail5.setTeacher(teacher3);
                avail5.setDay(Days.Viernes);
                avail5.setAmStart(LocalTime.of(10, 0));
                avail5.setAmEnd(LocalTime.of(14, 0));
                avail5.setPmStart(LocalTime.of(15, 0));
                avail5.setPmEnd(LocalTime.of(19, 0));
                availabilityRepo.save(avail5);

                System.out.println(">> Profesores y disponibilidad iniciales creados");
            }

            // Crear cursos si no existen
            if (courseRepo.count() == 0) {
                // Obtener las especializaciones creadas
                subjects math = subjectRepo.findBySubjectName("Matemáticas");
                subjects physics = subjectRepo.findBySubjectName("Física");
                subjects chemistry = subjectRepo.findBySubjectName("Química");

                teachers teacher1 = teacherRepo.findAll().stream().filter(t -> t.getTeacherName().equals("Juan Pérez")).findFirst().orElse(null);
                teachers teacher2 = teacherRepo.findAll().stream().filter(t -> t.getTeacherName().equals("María García")).findFirst().orElse(null);
                teachers teacher3 = teacherRepo.findAll().stream().filter(t -> t.getTeacherName().equals("Carlos López")).findFirst().orElse(null);

                // Asignar teacherSubject a cursos
                TeacherSubject ts1 = teacherSubjectRepo.findByTeacher_IdAndSubject_Id(teacher1.getId(), math.getId()).orElse(null);
                courses course1 = new courses();
                course1.setCourseName("1A");
                course1.setArea("Ciencias Básicas"); // Área asignada
                course1.setTeacherSubject(ts1);
                courseRepo.save(course1);

                TeacherSubject ts2 = teacherSubjectRepo.findByTeacher_IdAndSubject_Id(teacher2.getId(), physics.getId()).orElse(null);
                courses course2 = new courses();
                course2.setCourseName("2B");
                course2.setArea("Ciencias Básicas"); // Área asignada
                course2.setTeacherSubject(ts2);
                courseRepo.save(course2);

                TeacherSubject ts3 = teacherSubjectRepo.findByTeacher_IdAndSubject_Id(teacher3.getId(), chemistry.getId()).orElse(null);
                courses course3 = new courses();
                course3.setCourseName("3C");
                course3.setArea("Ciencias Básicas"); // Área asignada
                course3.setTeacherSubject(ts3);
                courseRepo.save(course3);

                System.out.println(">> Cursos iniciales creados");
            }
        };
    }
}
