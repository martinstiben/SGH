package com.horarios.SGH.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.horarios.SGH.Model.Role;
import com.horarios.SGH.Model.teachers;
import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.Iteachers;
import com.horarios.SGH.Repository.Iusers;
import com.horarios.SGH.DTO.LoginRequestDTO;
import com.horarios.SGH.DTO.LoginResponseDTO;
import com.horarios.SGH.DTO.RegisterRequestDTO;
import com.horarios.SGH.jwt.JwtTokenProvider;


@Service
public class AuthService {

    private final Iusers repo;
    private final Iteachers teacherRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileStorageService fileStorageService;

    public AuthService(Iusers repo,
                        Iteachers teacherRepo,
                        PasswordEncoder encoder,
                        AuthenticationManager authManager,
                        JwtTokenProvider jwtTokenProvider,
                        FileStorageService fileStorageService) {
        this.repo = repo;
        this.teacherRepo = teacherRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileStorageService = fileStorageService;
    }

    public String register(RegisterRequestDTO request, MultipartFile photo) {
        String username = request.getUsername();
        String rawPassword = request.getPassword();
        Role role = request.getRole();
        String teacherName = request.getTeacherName();

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }

        if (username.contains(" ")) {
            throw new IllegalArgumentException("El nombre de usuario no puede contener espacios");
        }

        if (!username.equals(username.toLowerCase())) {
            throw new IllegalArgumentException("El nombre de usuario no puede contener letras mayúsculas");
        }

        if (username.matches(".*\\d.*")) {
            throw new IllegalArgumentException("El nombre de usuario no puede contener números");
        }

        if (username.length() > 100) {
            throw new IllegalArgumentException("El nombre de usuario no puede exceder los 100 caracteres");
        }

        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }

        if (role == Role.MAESTRO && (teacherName == null || teacherName.trim().isEmpty())) {
            throw new IllegalArgumentException("El nombre del profesor es requerido para el rol MAESTRO");
        }

        repo.findByUserName(username).ifPresent(u -> {
            throw new IllegalStateException("El nombre de usuario ya está en uso");
        });

        users u = new users();
        u.setUserName(username);
        u.setPassword(encoder.encode(rawPassword));
        u.setRole(role);
        users savedUser = repo.save(u);

        // Si es profesor, crear registro en teachers
        if (role == Role.MAESTRO) {
            teachers teacher = new teachers();
            teacher.setTeacherName(teacherName);

            // Procesar foto si se proporcionó
            if (photo != null && !photo.isEmpty()) {
                String photoPath = fileStorageService.saveImageFile(photo, username);
                teacher.setPhotoPath(photoPath);
            }

            teacherRepo.save(teacher);
        }

        return "Usuario registrado correctamente";
    }


    public LoginResponseDTO login(LoginRequestDTO req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        String token = jwtTokenProvider.generateToken(req.getUsername());
        return new LoginResponseDTO(token);
    }

    public users getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return repo.findByUserName(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void updateUserName(String newName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        users user = repo.findByUserName(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setUserName(newName);
        repo.save(user);
    }
}