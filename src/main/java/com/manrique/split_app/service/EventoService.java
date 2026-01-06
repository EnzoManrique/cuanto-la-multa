package com.manrique.split_app.service;

import com.manrique.split_app.dto.BalanceDTO;
import com.manrique.split_app.dto.CrearEventoDTO;
import com.manrique.split_app.entity.*;
import com.manrique.split_app.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.manrique.split_app.dto.SugerenciaDeudaDTO;
import java.util.Comparator; // Importante para ordenar

@Service // Le dice a Spring: "Aquí hay lógica de negocio"
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    // @Transactional es clave: Si algo falla a la mitad, deshace toddo.
    // Evita que se cree un evento sin participantes si hay un error.
    @Transactional
    public Evento crearEvento(CrearEventoDTO dto) {

        // 1. Buscamos al usuario que está creando el evento
        // .orElseThrow() lanza un error si el ID no existe en la base de datos
        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Creamos el Evento
        Evento nuevoEvento = new Evento();
        nuevoEvento.setNombre(dto.getNombre());
        nuevoEvento.setFecha(LocalDate.now()); // Le ponemos la fecha de hoy

        // Guardamos el evento primero para que tenga un ID
        nuevoEvento = eventoRepository.save(nuevoEvento);

        // 3. LOGICA DE NEGOCIO:
        // Automáticamente agregamos al creador como el primer participante
        Participante primerParticipante = new Participante();
        primerParticipante.setNombre(creador.getNombre()); // Usamos su nombre real
        primerParticipante.setUsuario(creador); // Lo vinculamos a su cuenta
        primerParticipante.setEvento(nuevoEvento); // Lo metemos al evento

        participanteRepository.save(primerParticipante);

        // Devolvemos el evento creado por si el Controller quiere mostrarlo
        return nuevoEvento;
    }

    @Autowired
    private GastoRepository gastoRepository; // Inyectamos esto también
    @Autowired
    private DetalleGastoRepository detalleGastoRepository; // Y esto

    public List<BalanceDTO> calcularBalance(Long eventoId) {
        // 1. Buscamos el evento para obtener sus participantes
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        List<BalanceDTO> balances = new ArrayList<>();

        // 2. Recorremos cada participante (Juan, Manolo...)
        for (Participante p : evento.getParticipantes()) {

            // A. ¿Cuánto pagó? (Sumar todos sus tickets)
            List<Gasto> gastosPagados = gastoRepository.findByPagadorIdAndEventoId(p.getId(), eventoId);
            double totalPuesto = 0.0;
            for (Gasto g : gastosPagados) {
                totalPuesto += g.getMontoTotal();
            }

            // B. ¿Cuánto consumió? (Sumar todos sus detalles)
            List<DetalleGasto> consumos = detalleGastoRepository.findByParticipanteId(p.getId());
            double totalConsumido = 0.0;
            for (DetalleGasto d : consumos) {
                totalConsumido += d.getMontoCorrespondiente();
            }

            // C. La Resta Final
            double saldoFinal = totalPuesto - totalConsumido;

            // D. Guardamos en el DTO
            BalanceDTO dto = new BalanceDTO();
            dto.setNombreParticipante(p.getNombre());
            dto.setSaldo(saldoFinal);

            balances.add(dto);
        }

        return balances;
    }

    public List<SugerenciaDeudaDTO> calcularSugerencias(Long eventoId) {
        // 1. Obtenemos los balances crudos (lo que ya tienes hecho)
        List<BalanceDTO> balances = calcularBalance(eventoId);

        // 2. Separamos en dos listas: Los que Deben (-) y los que Reciben (+)
        List<BalanceDTO> deudores = new ArrayList<>();
        List<BalanceDTO> acreedores = new ArrayList<>();

        for (BalanceDTO b : balances) {
            if (b.getSaldo() < -0.01) deudores.add(b);       // Deben plata (usamos 0.01 para evitar errores de decimales)
            else if (b.getSaldo() > 0.01) acreedores.add(b); // Esperan plata
        }

        // 3. Ordenamos: Que paguen primero los que deben más, a los que se les debe más
        deudores.sort(Comparator.comparing(BalanceDTO::getSaldo)); // Orden ascendente (-100, -50...)
        acreedores.sort(Comparator.comparing(BalanceDTO::getSaldo).reversed()); // Descendente (100, 50...)

        List<SugerenciaDeudaDTO> sugerencias = new ArrayList<>();

        // 4. El Algoritmo de Emparejamiento (Greedy)
        int iDeudor = 0;
        int iAcreedor = 0;

        while (iDeudor < deudores.size() && iAcreedor < acreedores.size()) {
            BalanceDTO deudor = deudores.get(iDeudor);
            BalanceDTO acreedor = acreedores.get(iAcreedor);

            // Monto a saldar: El mínimo entre lo que uno debe y lo que el otro espera
            // Math.abs convierte el negativo a positivo para comparar
            double monto = Math.min(Math.abs(deudor.getSaldo()), acreedor.getSaldo());

            // Redondeamos a 2 decimales para que quede bonito
            monto = Math.round(monto * 100.0) / 100.0;

            sugerencias.add(new SugerenciaDeudaDTO(deudor.getNombreParticipante(), acreedor.getNombreParticipante(), monto));

            // Actualizamos los saldos temporales
            deudor.setSaldo(deudor.getSaldo() + monto);   // El deudor "pagó", su deuda baja (se acerca a 0)
            acreedor.setSaldo(acreedor.getSaldo() - monto); // El acreedor "cobró", su espera baja

            // Si el deudor ya pagó tod0, pasamos al siguiente deudor
            if (Math.abs(deudor.getSaldo()) < 0.01) {
                iDeudor++;
            }
            // Si el acreedor ya cobró tod0, pasamos al siguiente acreedor
            if (acreedor.getSaldo() < 0.01) {
                iAcreedor++;
            }
        }

        return sugerencias;
    }

}