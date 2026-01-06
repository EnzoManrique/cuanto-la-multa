package com.manrique.split_app.controller;

import com.manrique.split_app.dto.BalanceDTO;
import com.manrique.split_app.dto.CrearEventoDTO;
import com.manrique.split_app.entity.Evento;
import com.manrique.split_app.repository.EventoRepository;
import com.manrique.split_app.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.manrique.split_app.dto.SugerenciaDeudaDTO;

@RestController // Dice: "Yo hablo JSON y recibo peticiones web"
@RequestMapping("/api/eventos") // Todas las URLs empezarán con esto
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private EventoRepository eventoRepository;

    // Cuando alguien mande un POST a /api/eventos...
    @PostMapping
    public ResponseEntity<Evento> crear(@RequestBody CrearEventoDTO dto) {
        // ... llamamos al servicio para que haga el trabajo sucio
        Evento eventoCreado = eventoService.crearEvento(dto);

        // ... y respondemos "200 OK" con el evento creado
        return ResponseEntity.ok(eventoCreado);
    }

    @GetMapping("/{id}/balance") // La URL será: /api/eventos/1/balance
    public ResponseEntity<List<BalanceDTO>> obtenerBalance(@PathVariable Long id) {
        List<BalanceDTO> balances = eventoService.calcularBalance(id);
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/{id}/sugerencias") // URL: /api/eventos/1/sugerencias
    public ResponseEntity<List<SugerenciaDeudaDTO>> obtenerSugerencias(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.calcularSugerencias(id));
    }

    // ... otros métodos ...

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        if (!eventoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

