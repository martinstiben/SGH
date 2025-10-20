package com.horarios.SGH.Model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Roles disponibles en el sistema")
public enum Role {
    @Schema(description = "Profesor/Maestro") MAESTRO,
    @Schema(description = "Coordinador académico") COORDINADOR,
    @Schema(description = "Estudiante") ESTUDIANTE,
    @Schema(description = "Director de área") DIRECTOR_DE_AREA
}