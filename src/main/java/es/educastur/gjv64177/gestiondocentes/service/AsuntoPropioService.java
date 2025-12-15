package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.model.AsuntoPropio;
import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.repository.AsuntoPropioRepository;
import es.educastur.gjv64177.gestiondocentes.repository.DocenteRepository;
import es.educastur.gjv64177.gestiondocentes.util.MetodosAux;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsuntoPropioService {
	@Autowired
	private AsuntoPropioRepository asuntoPropioRepository;
	@Autowired
	private DocenteRepository docenteRepository;
	@Autowired
	private MetodosAux aux;

	public AsuntoPropio findById(Long id) {
		return asuntoPropioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asunto propio no encontrado con ID: " + id));
	}

	public List<AsuntoPropio> findAll() {
		return asuntoPropioRepository.findAll();
	}

	public List<AsuntoPropio> findByDocenteAndEstado(Long docenteId, String estado) {
		return asuntoPropioRepository.findByDocenteIdAndEstado(docenteId, estado);
	}

	public AsuntoPropio save(@Valid AsuntoPropio asuntoPropio) {
		return asuntoPropioRepository.save(asuntoPropio);
	}

	public void deleteById(Long id) {
		if (!asuntoPropioRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede borrar, no existe el ID: " + id);
		}
		asuntoPropioRepository.deleteById(id);
	}

	public List<AsuntoPropio> findByEstado(String pendiente) {
		return asuntoPropioRepository.findByEstado(pendiente);
	}

	public AsuntoPropio findByDocenteAndFecha(Long docenteId, LocalDate fecha) {
		return asuntoPropioRepository.findByDocenteIdAndDiaSolicitado(docenteId, fecha)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asunto propio no encontrado para el docente ID: " + docenteId + " y fecha: " + fecha));
	}

	public List<AsuntoPropio> findByDocente(Long docenteId) {
		return asuntoPropioRepository.findByDocenteId(docenteId);
	}

	public Docente topDocenteDiasDisfrutados() {
		List<Docente> topDocentes = asuntoPropioRepository.findTopDocentes("ACEPTADO", LocalDate.now(), PageRequest.of(0, 1));
		if (topDocentes.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nadie ha disfrutado días aún");
		}
		return topDocentes.getFirst();
	}

	public List<AsuntoPropio> diasAceptadosPorDisfrutar() {
		return asuntoPropioRepository.findByEstadoAndDiaSolicitadoAfter("ACEPTADO", LocalDate.now());
	}

	public List<AsuntoPropio> findByEstadoAndFechaBefore(String aceptado, LocalDate fechaActual) {
		return asuntoPropioRepository.findByEstadoAndDiaSolicitadoBefore(aceptado, fechaActual);
	}

	public List<AsuntoPropio> findByEstadoAndFechaAfter(String aceptado, LocalDate fechaActual) {
		return asuntoPropioRepository.findByEstadoAndDiaSolicitadoAfter(aceptado, fechaActual);
	}

	public AsuntoPropio solicitarDia(AsuntoPropio asuntoPropio) {
		Docente docenteReal = docenteRepository.findById(asuntoPropio.getDocente()
				                                                 .getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado con ID: " + asuntoPropio.getDocente()
						.getId()));
		if (!docenteReal.comprobarDia(aux.getIndiceTrimestre(asuntoPropio.getDiaSolicitado()))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El docente no tiene dias de asuntos propios disponibles.");
		}
		asuntoPropio.setDocente(docenteReal);
		boolean existente = asuntoPropioRepository.existsByDocenteIdAndDiaSolicitado(asuntoPropio.getDocente()
				                                                                             .getId(), asuntoPropio.getDiaSolicitado());

		if (existente) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una solicitud para este docente en la fecha solicitada.");
		}

		asuntoPropio.setEstado("PENDIENTE");
		asuntoPropio.setFechaPeticion(java.time.LocalDateTime.now());
		return asuntoPropioRepository.save(asuntoPropio);
	}

	public List<AsuntoPropio> listarAsuntosAceptadosFecha(String filtroFecha) {
		LocalDate fechaActual = LocalDate.now();
		if (filtroFecha.equalsIgnoreCase("PASADOS")) {
			return findByEstadoAndFechaBefore("ACEPTADO", fechaActual);
		} else if (filtroFecha.equalsIgnoreCase("FUTUROS")) {
			return findByEstadoAndFechaAfter("ACEPTADO", fechaActual);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filtro de fecha no valido. Debe ser 'PASADOS' o 'FUTUROS'.");
		}
	}

	@Transactional
	public Boolean validarAsunto(LocalDate fecha, Long docente_id, String estado) {
		if (!estado.equalsIgnoreCase("ACEPTADO") && !estado.equalsIgnoreCase("DENEGADO")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no valido. Debe ser 'ACEPTADO' o 'DENEGADO'.");
		}
		AsuntoPropio existente = findByDocenteAndFecha(docente_id, fecha);

		if (!existente.getEstado()
				.equalsIgnoreCase("PENDIENTE")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud ya está cerrada.");
		}

		existente.setEstado(estado.toUpperCase());
		if (estado.equalsIgnoreCase("ACEPTADO")) {
			if (!existente.getDocente()
					.comprobarDia(aux.getIndiceTrimestre(existente.getDiaSolicitado()))) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El docente no tiene dias de asuntos propios disponibles.");
			}
			existente.getDocente()
					.usarDia(aux.getIndiceTrimestre(fecha));
			docenteRepository.save(existente.getDocente());
		}
		save(existente);
		return estado.equalsIgnoreCase("ACEPTADO");
	}
}
