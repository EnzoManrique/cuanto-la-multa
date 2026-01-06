package com.manrique.split_app.controller;

import com.manrique.split_app.dto.CrearUsuarioDTO;
import com.manrique.split_app.entity.Evento;
import com.manrique.split_app.entity.Usuario;
import com.manrique.split_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody CrearUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.crearUsuario(dto));
    }

    @Autowired
    private com.manrique.split_app.repository.ParticipanteRepository participanteRepository;

    @GetMapping("/{id}/eventos") // URL: /api/usuarios/1/eventos
    public ResponseEntity<List<Evento>> obtenerMisEventos(@PathVariable Long id) {
        // 1. Buscamos dónde participa este usuario
        List<com.manrique.split_app.entity.Participante> participaciones = participanteRepository.findByUsuarioId(id);

        // 2. Sacamos la lista de Eventos de esas participaciones
        List<com.manrique.split_app.entity.Evento> misEventos = participaciones.stream()
                .map(p -> p.getEvento())
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(misEventos);
    }

}