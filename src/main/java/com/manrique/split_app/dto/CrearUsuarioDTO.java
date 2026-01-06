package com.manrique.split_app.dto;

import lombok.Data;

@Data
public class CrearUsuarioDTO {
    private String nombre;
    private String email;
    private String password;
    private String fotoUrl;
}