package com.horarios.SGH.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.Iusers;

@Service
public class usersService {

    @Autowired
    private Iusers usersRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Optional<users> findById(int userId) {
        try {
            return usersRepository.findById(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el usuario con ID: " + userId + ", Error: " + e.getMessage());
        }
    }

    // Método de login removido - ahora se maneja en AuthService con 2FA

    /**
     * Actualiza la foto de perfil de un usuario.
     * @param userId ID del usuario
     * @param photo Archivo de imagen para la foto de perfil
     * @return Mensaje de confirmación
     */
    public String updateUserPhoto(int userId, MultipartFile photo) {
        try {
            Optional<users> userOpt = usersRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            users user = userOpt.get();

            if (photo != null && !photo.isEmpty()) {
                FileStorageService.PhotoData photoData = fileStorageService.processImageFile(photo);
                user.setPhotoData(photoData.getData());
                user.setPhotoContentType(photoData.getContentType());
                user.setPhotoFileName(photoData.getFileName());
            } else {
                // Si photo es null o vacío, eliminar foto existente
                user.setPhotoData(null);
                user.setPhotoContentType(null);
                user.setPhotoFileName(null);
            }

            usersRepository.save(user);
            return "Foto de perfil actualizada correctamente";

        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar excepciones de validación
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la foto de perfil: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la información completa de un usuario incluyendo foto.
     * @param userId ID del usuario
     * @return DTO con información del usuario
     */
    public Optional<com.horarios.SGH.DTO.usersDTO> getUserWithPhoto(int userId) {
        try {
            return usersRepository.findById(userId)
                .map(user -> {
                    com.horarios.SGH.DTO.usersDTO dto = new com.horarios.SGH.DTO.usersDTO();
                    dto.setUserId(user.getUserId());
                    dto.setUserName(user.getUserName());
                    dto.setPassword(user.getPassword());
                    dto.setRole(user.getRole());
                    dto.setPhotoData(user.getPhotoData());
                    dto.setPhotoContentType(user.getPhotoContentType());
                    dto.setPhotoFileName(user.getPhotoFileName());
                    return dto;
                });
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el usuario: " + e.getMessage(), e);
        }
    }
}