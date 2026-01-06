package com.manrique.split_app.controller;

import com.manrique.split_app.dto.AgregarParticipanteDTO;
import com.manrique.split_app.entity.Participante;
import com.manrique.split_app.repository.ParticipanteRepository;
import com.manrique.split_app.service.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @PostMapping
    public ResponseEntity<Participante> agregar(@RequestBody AgregarParticipanteDTO dto) {
        return ResponseEntity.ok(participanteService.agregarParticipante(dto));
    }

    @GetMapping("/evento/{eventoId}") // URL: /api/participantes/evento/1
    public ResponseEntity<List<Participante>> listarPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(participanteRepository.findByEventoId(eventoId));
    }

    // ... otros métodos ...

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarParticipante(@PathVariable Long id) {
        if (!participanteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            participanteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Si el participante ya tiene gastos, la base de datos no dejará borrarlo
            // para no romper las matemáticas. Devolvemos un error 409 (Conflicto).
            return ResponseEntity.status(409).body("No se puede borrar: tiene gastos asociados.");
        }
    }
}
