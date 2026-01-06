package com.manrique.split_app.service;

import com.manrique.split_app.dto.CrearUsuarioDTO;
import com.manrique.split_app.entity.Usuario;
import com.manrique.split_app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(CrearUsuarioDTO dto) {
        // 1. Buscamos si ya existe el email
        return usuarioRepository.findByEmail(dto.getEmail())
                .map(usuarioExistente -> {
                    // ESCENARIO A: El usuario YA EXISTE.
                    // Verificamos si la contraseña coincide
                    if (!usuarioExistente.getPassword().equals(dto.getPassword())) {
                        throw new RuntimeException("¡Contraseña incorrecta!");
                        // Esto devolverá un error 500 al frontend
                    }
                    return usuarioExistente;
                })
                .orElseGet(() -> {
                    // ESCENARIO B: El usuario NO EXISTE. Lo creamos.
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setNombre(dto.getNombre());
                    nuevoUsuario.setEmail(dto.getEmail());
                    nuevoUsuario.setPassword(dto.getPassword()); // Guardamos la contraseña
                    nuevoUsuario.setFotoUrl(dto.getFotoUrl());
                    return usuarioRepository.save(nuevoUsuario);
                });
    }
}