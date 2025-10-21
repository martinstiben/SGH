package com.horarios.SGH.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Servicio responsable del manejo de archivos subidos.
 * Principio de responsabilidad única (SRP): Solo maneja el almacenamiento de archivos.
 */
@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;

    /**
     * Guarda un archivo de imagen en el directorio configurado.
     * @param file Archivo multipart a guardar
     * @param username Nombre de usuario para generar el nombre del archivo
     * @return Nombre del archivo guardado
     * @throws IllegalArgumentException Si el archivo no es válido
     * @throws RuntimeException Si ocurre un error de I/O
     */
    public String saveImageFile(MultipartFile file, String username) {
        validateImageFile(file);

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = generateUniqueFilename(file, username);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el archivo sea una imagen válida y no exceda el tamaño máximo.
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        // Validar tamaño máximo (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo no puede exceder los 5MB");
        }
    }

    /**
     * Genera un nombre único para el archivo basado en el username y un UUID.
     */
    private String generateUniqueFilename(MultipartFile file, String username) {
        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg"; // default

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return username + "_" + UUID.randomUUID().toString() + extension;
    }
}