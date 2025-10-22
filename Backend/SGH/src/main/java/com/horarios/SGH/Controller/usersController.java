package com.horarios.SGH.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.horarios.SGH.DTO.responseDTO;
import com.horarios.SGH.Model.users;
import com.horarios.SGH.Service.usersService;
import com.horarios.SGH.Repository.Iusers;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
@RestController
@RequestMapping("/users")
public class usersController {

    @Autowired
    private usersService usersService;

    @Autowired
    private Iusers usersRepository;

    @Value("${app.master.username}")
    private String masterUsername;

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        try {
            Optional<users> usuarioOptional = usersService.findById(id);
            if (usuarioOptional.isPresent()) {
                return ResponseEntity.ok(usuarioOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new responseDTO("ERROR", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new responseDTO("ERROR", "Error interno: " + e.getMessage()));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<responseDTO> login(@RequestParam String userName, @RequestParam String password) {
        try {

            // Validación de campos
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new responseDTO("ERROR", "El nombre de usuario no puede estar vacío"));
            }

            if (userName.contains(" ")) {
                return ResponseEntity.badRequest().body(new responseDTO("ERROR", "El nombre de usuario no puede contener espacios"));
            }

            // Validación que el nombre de usuario no tenga Mayusculas
            if (!userName.equals(userName.toLowerCase())) {
                return ResponseEntity.badRequest().body(new responseDTO("ERROR", "El nombre de usuario no puede contener letras mayúsculas"));
            }

            // Validación que el nombre de usuario no tenga números
            if (userName.matches(".*\\d.*")) {
                return ResponseEntity.badRequest().body(new responseDTO("ERROR", "El nombre de usuario no puede contener números"));
            }
            
            // Validación de longitud del nombre de usuario
            if (userName.length() > 100) {
                return ResponseEntity.badRequest().body(new responseDTO("ERROR", "El nombre de usuario no puede exceder los 100 caracteres"));
            }

            // Validación de contraseña
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new responseDTO("ERROR", "La contraseña no puede estar vacía"));
            }

            // Consulta en base de datos
            Optional<users> usuario = usersRepository.findByUserName(userName);
            if (!usuario.isPresent() || !usuario.get().getUserName().equals(userName)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new responseDTO("ERROR", "Usuario no encontrado"));
            }

            // Lógica de autenticación
            String resultMessage = usersService.login(userName, password);
            return ResponseEntity.ok(new responseDTO("OK", resultMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new responseDTO("ERROR", e.getMessage()));
        }
    }

    // Eliminar usuario (excepto master)
    @DeleteMapping("/{username}")
    public ResponseEntity<responseDTO> deleteUser(@PathVariable String username) {
        try {
            if (username.equalsIgnoreCase(masterUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new responseDTO("ERROR", "No se puede eliminar el usuario master"));
            }

            Optional<users> usuario = usersRepository.findByUserName(username);
            if (!usuario.isPresent() || !usuario.get().getUserName().equals(username)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new responseDTO("ERROR", "Usuario no encontrado"));
            }

            usersRepository.delete(usuario.get());
            return ResponseEntity.ok(new responseDTO("OK", "Usuario eliminado correctamente"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new responseDTO("ERROR", "Error interno: " + e.getMessage()));
        }
    }

    // Actualizar foto de perfil
    @PutMapping("/{id}/photo")
    public ResponseEntity<responseDTO> updateUserPhoto(@PathVariable int id, @RequestParam("photo") MultipartFile photo) {
        try {
            String result = usersService.updateUserPhoto(id, photo);
            return ResponseEntity.ok(new responseDTO("OK", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new responseDTO("ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new responseDTO("ERROR", "Error al actualizar foto: " + e.getMessage()));
        }
    }

    // Eliminar foto de perfil
    @DeleteMapping("/{id}/photo")
    public ResponseEntity<responseDTO> deleteUserPhoto(@PathVariable int id) {
        try {
            String result = usersService.updateUserPhoto(id, null);
            return ResponseEntity.ok(new responseDTO("OK", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new responseDTO("ERROR", "Error al eliminar foto: " + e.getMessage()));
        }
    }

    // Obtener foto de perfil
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable int id) {
        try {
            Optional<com.horarios.SGH.DTO.usersDTO> userOpt = usersService.getUserWithPhoto(id);
            if (!userOpt.isPresent() || userOpt.get().getPhotoData() == null) {
                return ResponseEntity.notFound().build();
            }

            com.horarios.SGH.DTO.usersDTO user = userOpt.get();
            return ResponseEntity.ok()
                    .header("Content-Type", user.getPhotoContentType() != null ? user.getPhotoContentType() : "image/jpeg")
                    .header("Content-Disposition", "inline; filename=\"" + (user.getPhotoFileName() != null ? user.getPhotoFileName() : "photo.jpg") + "\"")
                    .body(user.getPhotoData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}