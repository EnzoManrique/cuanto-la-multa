package com.manrique.split_app.repository;

import com.manrique.split_app.entity.DetalleGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleGastoRepository extends JpaRepository<DetalleGasto, Long> {
    // Busca todos los items que consumió el participante X en el evento...
    // Ojo: Detalle no tiene "eventoId" directo, tiene "Gasto".
    // Así que buscamos por participante directamente (asumiendo que el ID de participante es único por evento)
    List<DetalleGasto> findByParticipanteId(Long participanteId);
}