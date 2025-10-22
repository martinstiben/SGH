package com.horarios.SGH.Controller;

import com.horarios.SGH.DTO.LoginRequestDTO;
import com.horarios.SGH.DTO.LoginResponseDTO;
import com.horarios.SGH.DTO.RegisterRequestDTO;
import com.horarios.SGH.Model.Role;
import com.horarios.SGH.Service.AuthService;
import com.horarios.SGH.Service.TokenRevocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private final AuthService service;
    private final TokenRevocationService tokenRevocationService;
    private final com.horarios.SGH.Service.usersService usersService;

    public AuthController(AuthService service, TokenRevocationService tokenRevocationService, com.horarios.SGH.Service.usersService usersService) {
        this.service = service;
        this.tokenRevocationService = tokenRevocationService;
        this.usersService = usersService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO resp = service.login(request);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario con rol específico y foto opcional")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en el registro")
    })
    public ResponseEntity<?> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("role") Role role,
            @RequestParam(value = "teacherName", required = false) String teacherName,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            RegisterRequestDTO request = new RegisterRequestDTO();
            request.setUsername(username);
            request.setPassword(password);
            request.setRole(role);
            request.setTeacherName(teacherName);

            String msg = service.register(request, photo);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                tokenRevocationService.revokeToken(token);
                return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Token no proporcionado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al cerrar sesión"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            var user = service.getProfile();
            return ResponseEntity.ok(Map.of(
                "name", user.getUserName(),
                "role", getRoleLabel(user.getRole())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error obteniendo perfil"));
        }
    }

    @PutMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfile(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            // Validar que al menos un campo esté presente
            if ((name == null || name.trim().isEmpty()) && (photo == null || photo.isEmpty())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Debe proporcionar al menos un campo para actualizar"));
            }

            // Actualizar nombre si se proporcionó
            if (name != null && !name.trim().isEmpty()) {
                service.updateUserName(name);
            }

            // Actualizar foto si se proporcionó
            if (photo != null && !photo.isEmpty()) {
                var user = service.getProfile();
                usersService.updateUserPhoto(user.getUserId(), photo);
            }

            return ResponseEntity.ok(Map.of("message", "Perfil actualizado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error actualizando perfil"));
        }
    }


    @GetMapping("/roles")
    @Operation(summary = "Obtener roles disponibles", description = "Devuelve la lista de roles disponibles para registro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente")
    })
    public ResponseEntity<?> getRoles() {
        try {
            List<Map<String, String>> roles = Arrays.stream(Role.values())
                .map(role -> Map.of(
                    "value", role.name(),
                    "label", getRoleLabel(role)
                ))
                .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("roles", roles));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error obteniendo roles"));
        }
    }

    private String getRoleLabel(Role role) {
        switch (role) {
            case MAESTRO:
                return "Maestro";
            case COORDINADOR:
                return "Coordinador";
            case ESTUDIANTE:
                return "Estudiante";
            case DIRECTOR_DE_AREA:
                return "Director de Área";
            default:
                return role.name();
        }
    }
}