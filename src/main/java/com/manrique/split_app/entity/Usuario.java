package com.manrique.split_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data; // Esto viene de Lombok
import lombok.NoArgsConstructor;


@Entity // Le dice a Spring: "Esto es una tabla en la BD"
@Data   // Le dice a Lombok: "Créame los Getters, Setters y toString automáticamente"
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID Autoincremental (1, 2, 3...)
    private Long id;

    private String nombre;
    @Column(unique = true)
    private String email;
    private String password;
    private String fotoUrl; // Para la foto que decías
}
