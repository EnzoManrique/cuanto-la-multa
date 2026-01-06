package com.manrique.split_app.service;

import com.manrique.split_app.dto.AgregarParticipanteDTO;
import com.manrique.split_app.entity.*;
import com.manrique.split_app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;
    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Participante agregarParticipante(AgregarParticipanteDTO dto) {
        // 1. Buscamos el evento
        Evento evento = eventoRepository.findById(dto.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // 2. Preparamos el participante
        Participante nuevo = new Participante();
        nuevo.setNombre(dto.getNombre());
        nuevo.setEvento(evento);

        // 3. Si viene con ID de usuario, lo vinculamos
        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            nuevo.setUsuario(usuario);
            // Si el nombre venía vacío, usamos el del usuario real
            if (nuevo.getNombre() == null || nuevo.getNombre().isEmpty()) {
                nuevo.setNombre(usuario.getNombre());
            }
        }

        return participanteRepository.save(nuevo);
    }
}