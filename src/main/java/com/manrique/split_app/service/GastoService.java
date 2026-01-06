package com.manrique.split_app.service;

import com.manrique.split_app.dto.CrearGastoDTO;
import com.manrique.split_app.entity.*;
import com.manrique.split_app.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private DetalleGastoRepository detalleGastoRepository;

    @Transactional
    public Gasto crearGasto(CrearGastoDTO dto) {

        // 1. Validar que el que paga existe
        Participante pagador = participanteRepository.findById(dto.getPagadorId())
                .orElseThrow(() -> new RuntimeException("El pagador no existe"));

        // 2. Crear el Gasto "Padre" (El ticket)
        Gasto nuevoGasto = new Gasto();
        nuevoGasto.setTitulo(dto.getTitulo());
        nuevoGasto.setMontoTotal(dto.getMonto());
        nuevoGasto.setImagenUrl(dto.getImagenUrl()); // Tu URL de la foto
        nuevoGasto.setFecha(LocalDateTime.now());
        nuevoGasto.setPagador(pagador);
        nuevoGasto.setEvento(pagador.getEvento()); // El gasto pertenece al mismo evento que el pagador

        nuevoGasto = gastoRepository.save(nuevoGasto);

        // 3. MATEMÁTICA: Dividir entre los seleccionados
        List<Long> consumidoresIds = dto.getConsumidoresIds();

        if (consumidoresIds == null || consumidoresIds.isEmpty()) {
            throw new RuntimeException("Debes seleccionar al menos una persona para dividir");
        }

        // Cálculo simple: Total / Cantidad de personas
        Double montoIndividual = dto.getMonto() / consumidoresIds.size();

        // 4. Crear los detalles (El Bucle)
        for (Long idConsumidor : consumidoresIds) {
            Participante consumidor = participanteRepository.findById(idConsumidor)
                    .orElseThrow(() -> new RuntimeException("Uno de los consumidores no existe"));

            DetalleGasto detalle = new DetalleGasto();
            detalle.setGasto(nuevoGasto);
            detalle.setParticipante(consumidor);
            detalle.setMontoCorrespondiente(montoIndividual);

            detalleGastoRepository.save(detalle);
        }

        return nuevoGasto;
    }
}