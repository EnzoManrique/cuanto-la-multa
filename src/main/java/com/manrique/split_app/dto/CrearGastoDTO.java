package com.manrique.split_app.dto;

import lombok.Data;
import java.util.List;

@Data
public class CrearGastoDTO {
    private String titulo;              // "Carne"
    private Double monto;               // 50000
    private String imagenUrl;           // La foto del ticket (opcional)

    private Long pagadorId;             // El ID del Participante que pagó
    private List<Long> consumidoresIds; // Lista de IDs de los Participantes que deben pagar
}