package com.manrique.split_app.dto;

import lombok.Data;

@Data
public class CrearEventoDTO {
    private String nombre;        // Ej: "Viaje a la Costa"
    private Long creadorId;       // El ID del usuario que está logueado creando el grupo
}