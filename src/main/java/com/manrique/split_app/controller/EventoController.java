package com.manrique.split_app.controller;

import com.manrique.split_app.dto.BalanceDTO;
import com.manrique.split_app.dto.CrearEventoDTO;
import com.manrique.split_app.entity.Evento;
import com.manrique.split_app.entity.Usuario;
import com.manrique.split_app.repository.EventoRepository;
import com.manrique.split_app.repository.UsuarioRepository;
import com.manrique.split_app.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.manrique.split_app.dto.SugerenciaDeudaDTO;

@RestController // Dice: "Yo hablo JSON y recibo peticiones web"
@RequestMapping("/api/eventos") // Todas las URLs empezarán con esto
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private EventoRepository eventoRepository;

    // Cuando alguien mande un POST a /api/eventos...
    @Autowired
    private UsuarioRepository usuarioRepository; // Necesitamos esto para buscar al dueño

    @PostMapping
    public ResponseEntity<Evento> createEvento(@RequestBody Map<String, Object> payload) {
        // 1. Sacamos los datos del JSON manual (porque viene mezclado)
        String nombre = (String) payload.get("nombre");
        Integer creadorId = (Integer) payload.get("creadorId");

        // 2. Creamos el evento
        Evento nuevoEvento = new Evento();
        nuevoEvento.setNombre(nombre);
        nuevoEvento.setFecha(LocalDate.now());

        // 3. Buscamos al usuario y se lo asignamos
        if (creadorId != null) {
            Usuario usuario = usuarioRepository.findById(Long.valueOf(creadorId)).orElse(null);
            nuevoEvento.setCreador(usuario);
        }

        // 4. Guardamos
        Evento eventoGuardado = eventoRepository.save(nuevoEvento);
        return ResponseEntity.ok(eventoGuardado);
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

