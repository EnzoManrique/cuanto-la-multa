package com.manrique.split_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DetalleGasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cuánto le corresponde pagar a ESTA persona de ese gasto.
    // Ej: 5000.00
    private Double montoCorrespondiente;

    // RELACIÓN: Pertenece a un Gasto padre
    @ManyToOne
    @JoinColumn(name = "gasto_id")
    @JsonIgnore
    private Gasto gasto;

    // RELACIÓN: Quién es el que tiene que pagar esta parte (El consumidor)
    @ManyToOne
    @JoinColumn(name = "participante_id")
    private Participante participante;
}