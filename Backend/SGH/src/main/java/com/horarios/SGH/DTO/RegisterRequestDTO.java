package com.horarios.SGH.DTO;

import com.horarios.SGH.Model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para solicitud de registro de usuario")
public class RegisterRequestDTO {
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 100, message = "El nombre de usuario no puede exceder los 100 caracteres")
    @Pattern(regexp = "^[a-z]*$", message = "El nombre de usuario solo puede contener letras minúsculas")
    @Schema(description = "Nombre de usuario (solo letras minúsculas)", example = "usuarioejemplo", required = true)
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "La contraseña debe contener al menos una letra minúscula, una mayúscula y un número")
    @Schema(description = "Contraseña (debe contener minúscula, mayúscula y número)", example = "Password123", required = true)
    private String password;

    @NotBlank(message = "El email no puede estar vacío")
    @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "El email debe tener un formato válido")
    @Schema(description = "Email del usuario", example = "usuario@example.com", required = true)
    private String email;

    @NotNull(message = "El rol no puede ser nulo")
    @Schema(description = "Rol del usuario", example = "MAESTRO", required = true, allowableValues = {"MAESTRO", "ESTUDIANTE"})
    private Role role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}