package com.horarios.SGH.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.horarios.SGH.Model.Role;
import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.Iusers;
import com.horarios.SGH.DTO.LoginRequestDTO;
import com.horarios.SGH.DTO.LoginResponseDTO;
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

    public String register(String username, String rawPassword, String email, Role role) {
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

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }

        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }

        repo.findByUserName(username).ifPresent(u -> {
            throw new IllegalStateException("El nombre de usuario ya está en uso");
        });

        repo.findByEmail(email).ifPresent(u -> {
            throw new IllegalStateException("El email ya está en uso");
        });

        users u = new users();
        u.setUserName(username);
        u.setPassword(encoder.encode(rawPassword));
        u.setEmail(email);
        u.setRole(role);
        repo.save(u);
        return "Usuario registrado correctamente";
    }

    public LoginResponseDTO login(LoginRequestDTO req) {
        // Buscar usuario por email
        users user = repo.findByEmail(req.getUsername())
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // Autenticar con el username del usuario encontrado
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUserName(), req.getPassword())
        );

        String token = jwtTokenProvider.generateToken(user.getUserName());
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