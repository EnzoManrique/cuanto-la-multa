package com.manrique.split_app.dto;

import lombok.Data;

@Data
public class AgregarParticipanteDTO {
    private String nombre;  // Obligatorio (Ej: "Manolo" o "Pedro")
    private Long eventoId;  // Obligatorio (A qué evento va)
    private Long usuarioId; // Opcional (Si es null, es un amigo sin cuenta)
}