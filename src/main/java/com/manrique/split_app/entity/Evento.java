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

    private String nombre;

    private LocalDate fecha;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<Participante> participantes;

    // --- AGREGAMOS ESTO QUE FALTABA ---
    // Relación: Muchos eventos pueden ser creados por un solo Usuario
    @ManyToOne
    @JoinColumn(name = "creador_id") // En la base de datos se llamará 'creador_id'
    private Usuario creador;
}