package com.manrique.split_app.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SugerenciaDeudaDTO {
    private String deudor;      // Quién paga
    private String acreedor;    // Quién cobra
    private Double monto;       // Cuánto
}