package com.manrique.split_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo; // Ej: "Supermercado"

    private Double montoTotal; // Ej: 20000.00

    private String imagenUrl; // ¡Aquí guardaremos la foto del ticket!

    private LocalDateTime fecha; // Para saber cuándo fue

    // RELACIÓN: Un gasto pertenece a un Evento específico
    @ManyToOne
    @JoinColumn(name = "evento_id")
    @JsonIgnore
    private Evento evento;

    // RELACIÓN: Un gasto lo pagó UNA persona (El que puso la tarjeta)
    @ManyToOne
    @JoinColumn(name = "pagador_id")
    private Participante pagador;

    // RELACIÓN: Un gasto se divide en muchos "pedacitos" (detalles)
    @OneToMany(mappedBy = "gasto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleGasto> detalles;
}