package com.manrique.split_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Juan" (O el nombre del usuario si existe)

    // RELACIÓN: Muchos participantes pertenecen a Un Evento.
    @ManyToOne
    @JoinColumn(name = "evento_id") // Esto crea la columna "evento_id" en la tabla
    @JsonIgnore
    private Evento evento;

    // RELACIÓN OPCIONAL: Un participante PUEDE ser un Usuario real de la app.
    // Si es null, es un "usuario fantasma" (amigo sin app).
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}