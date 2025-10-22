package com.horarios.SGH.Model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Roles disponibles en el sistema")
public enum Role {
    @Schema(description = "Administrador del sistema") ADMIN,
    @Schema(description = "Coordinador") COORDINADOR,
    @Schema(description = "Profesor/Maestro") MAESTRO,
    @Schema(description = "Estudiante") ESTUDIANTE,
    @Schema(description = "Director de Área") DIRECTOR_DE_AREA
}