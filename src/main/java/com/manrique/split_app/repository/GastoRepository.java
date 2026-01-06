package com.manrique.split_app.repository;

import com.manrique.split_app.entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// ... imports ...
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    // Busca todos los gastos donde el pagador sea X y pertenezcan al evento Y
    List<Gasto> findByPagadorIdAndEventoId(Long pagadorId, Long eventoId);

    List<Gasto> findByEventoId(Long eventoId);
}