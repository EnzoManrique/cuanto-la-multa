package com.manrique.split_app.repository;

import com.manrique.split_app.entity.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    List<Participante> findByUsuarioId(Long usuarioId);

    List<Participante> findByEventoId(Long eventoId);

}

