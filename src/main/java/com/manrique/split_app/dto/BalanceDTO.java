package com.manrique.split_app.dto;

import lombok.Data;

@Data
public class BalanceDTO {
    private String nombreParticipante;
    private Double saldo; // Si es positivo (a favor), si es negativo (deuda)
}