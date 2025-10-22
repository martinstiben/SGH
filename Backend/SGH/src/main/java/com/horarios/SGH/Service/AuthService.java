package com.horarios.SGH.Service;


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
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(Iusers repo,
                        PasswordEncoder encoder,
                        AuthenticationManager authManager,
                        JwtTokenProvider jwtTokenProvider) {
        this.repo = repo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String register(String name, String email, String rawPassword, Role role) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede estar vacío");
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("El correo electrónico debe tener un formato válido");
        }

        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }

        repo.findByUserName(email).ifPresent(u -> {
            throw new IllegalStateException("El correo electrónico ya está en uso");
        });

        users u = new users();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPassword));
        u.setRole(role);
        users savedUser = repo.save(u);

        return "Usuario registrado correctamente";
    }


    public String initiateLogin(LoginRequestDTO req) {
        // Verificar credenciales
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        // Generar código de verificación
        String verificationCode = generateVerificationCode();

        // Guardar código en la base de datos con expiración
        users user = repo.findByUserName(req.getEmail()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setVerificationCode(verificationCode);
        user.setCodeExpiration(java.time.LocalDateTime.now().plusMinutes(10)); // Expira en 10 minutos
        repo.save(user);

        // Enviar email con el código (simulado por ahora)
        sendVerificationEmail(user.getUserName(), verificationCode);

        return "Código de verificación enviado al correo electrónico";
    }

    public LoginResponseDTO verifyCode(String email, String code) {
        users user = repo.findByUserName(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new RuntimeException("Código de verificación inválido");
        }

        if (user.getCodeExpiration().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Código de verificación expirado");
        }

        // Limpiar código después de uso exitoso
        user.setVerificationCode(null);
        user.setCodeExpiration(null);
        repo.save(user);

        // Generar token JWT
        String token = jwtTokenProvider.generateToken(email);
        return new LoginResponseDTO(token);
    }

    private String generateVerificationCode() {
        java.util.Random random = new java.util.Random();
        int code = 100000 + random.nextInt(900000); // Código de 6 dígitos
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String email, String code) {
        // Simulación de envío de email - en producción usar JavaMailSender
        System.out.println("Código de verificación para " + email + ": " + code);
    }

    public users getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return repo.findByUserName(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void updateUserName(String newName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        users user = repo.findByUserName(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setName(newName);
        repo.save(user);
    }
}