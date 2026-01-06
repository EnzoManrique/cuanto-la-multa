package com.manrique.split_app.controller;

import com.manrique.split_app.dto.CrearGastoDTO;
import com.manrique.split_app.entity.Gasto;
import com.manrique.split_app.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @Autowired
    private com.manrique.split_app.repository.GastoRepository gastoRepository; // Inyectar repo

    @PostMapping
    public ResponseEntity<Gasto> crear(@RequestBody CrearGastoDTO dto) {
        Gasto gastoCreado = gastoService.crearGasto(dto);
        return ResponseEntity.ok(gastoCreado);
    }

    @GetMapping("/evento/{eventoId}") // URL: /api/gastos/evento/1
    public ResponseEntity<List<Gasto>> listarPorEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(gastoRepository.findByEventoId(eventoId));
    }

    @DeleteMapping("/{id}") // URL: DELETE /api/gastos/1
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        if (!gastoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        gastoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}