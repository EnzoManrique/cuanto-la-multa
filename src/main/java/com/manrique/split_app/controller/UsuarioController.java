package com.manrique.split_app.controller;

import com.manrique.split_app.entity.Evento;
import com.manrique.split_app.entity.Usuario;
import com.manrique.split_app.repository.UsuarioRepository;
import com.manrique.split_app.repository.EventoRepository; // <--- IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository; // <--- Inyectamos esto

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Login corregido (con .orElse(null))
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario loginRequest) {
        Usuario usuarioEncontrado = usuarioRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (usuarioEncontrado == null) {
            return ResponseEntity.status(401).body(null);
        }

        if (usuarioEncontrado.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(usuarioEncontrado);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    // Endpoint corregido: Busca en eventoRepository en vez de usar .getEventos()
    @GetMapping("/{id}/eventos")
    public ResponseEntity<List<Evento>> getEventosPorUsuario(@PathVariable Long id) {
        // CAMBIO AQUÍ: Agrega el guion bajo también
        List<Evento> eventos = eventoRepository.findByCreador_Id(id);
        return ResponseEntity.ok(eventos);
    }
}