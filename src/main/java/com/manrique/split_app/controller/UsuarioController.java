package com.manrique.split_app.controller;

import com.manrique.split_app.dto.CrearUsuarioDTO;
import com.manrique.split_app.entity.Evento;
import com.manrique.split_app.entity.Usuario;
import com.manrique.split_app.repository.UsuarioRepository;
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

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    // ... otros métodos ...

    // NUEVO: Endpoint específico para Login
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario loginRequest) {
        // 1. Buscamos al usuario por su email
        Usuario usuarioEncontrado = usuarioRepository.findByEmail(loginRequest.getEmail());

        // 2. Si no existe, error
        if (usuarioEncontrado == null) {
            return ResponseEntity.status(401).body(null); // 401 = No autorizado
        }

        // 3. Si existe, verificamos la contraseña
        // (Nota: En una app real aquí se encriptaría, pero para empezar está bien así)
        if (usuarioEncontrado.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(usuarioEncontrado);
        } else {
            return ResponseEntity.status(401).body(null); // Contraseña mal
        }
    }
}

}