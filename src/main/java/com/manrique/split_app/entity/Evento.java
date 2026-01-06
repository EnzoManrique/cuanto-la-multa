package com.manrique.split_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Asado Fin de Año"

    private LocalDate fecha; // Ej: 2025-01-05

    // RELACIÓN: Un evento tiene muchos participantes.
    // "mappedBy" dice: "La variable 'evento' en la clase Participante es la dueña de la relación"
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<Participante> participantes;
}