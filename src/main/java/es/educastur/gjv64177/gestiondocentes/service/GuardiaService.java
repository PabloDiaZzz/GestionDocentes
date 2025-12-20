package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.config.MainConfigProperties;
import es.educastur.gjv64177.gestiondocentes.dto.FaltaDTO;
import es.educastur.gjv64177.gestiondocentes.model.AsuntoPropio;
import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.model.Guardia;
import es.educastur.gjv64177.gestiondocentes.model.Horario;
import es.educastur.gjv64177.gestiondocentes.repository.AsuntoPropioRepository;
import es.educastur.gjv64177.gestiondocentes.repository.DocenteRepository;
import es.educastur.gjv64177.gestiondocentes.repository.GuardiaRepository;
import es.educastur.gjv64177.gestiondocentes.repository.HorarioRepository;
import es.educastur.gjv64177.gestiondocentes.util.MetodosAux;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuardiaService {

	@Autowired
	private DocenteRepository docenteRepository;
	@Autowired
	private GuardiaRepository guardiaRepository;
	@Autowired
	private HorarioRepository horarioRepository;
	@Autowired
	private AsuntoPropioRepository asuntoPropioRepository;
	@Autowired
	private MetodosAux aux;
	@Autowired
	private MainConfigProperties config;

	@Transactional
	public Docente buscarSustituto(Horario horario) {
		if (horario.getDocente() == null) { // Por si el horario está corrupto y no tiene docente asignado
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha encontrado docente para el dia "
					+ horario.getDia() + " y la hora " + horario.getHora() + " el horario puede estar corrupto");
		}
		List<Docente> candidatos = docenteRepository.findAll();
		// Borramos el docente da la clase que es el que falta
		candidatos.removeIf(d -> d.getId()
				.equals(horario.getDocente()
						.getId()));
		// Borramos los que tienen clase
		candidatos.removeAll(horarioRepository.findByDiaAndHora(horario.getDia(), horario.getHora())
				.stream()
				.map(Horario::getDocente)
				.toList());

		// Primero buscamos si hay alguien del mismo departamento
		Docente dept = candidatosMismoDept(candidatos, (horario.getDocente()));
		if (dept != null) { // Si encuentra devolvemos ese docente
			return dept;
		}

		// Luego buscamos alguien del ciclo
		Docente mismoGrupo = candidatosMismoGrupo(candidatos, horario);
		if (mismoGrupo != null) {
			return mismoGrupo;
		}

		// Si no buscamos alguien del centro con menos guardias realizadas
		return getConMenosGuardias(candidatos).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
				"No se ha encontrado sustituto para el dia " + horario.getDia() + " y la hora " + horario.getHora()
						+ " para el profesor " + horario.getDocente()
								.getSiglas()));
	}

	private Docente candidatosMismoGrupo(List<Docente> todos, Horario horario) {
		// Seguridad ante nulls
		if (todos == null || todos.isEmpty() || horario == null || horario.getDocente() == null
				|| horario.getAsignatura() == null || horario.getAsignatura()
						.getCiclo() == null) {
			return null;
		}

		// Filtramos los del mismo departamento y buscamos el que tiene menos guardias
		return getConMenosGuardias(todos.stream()
				.filter(d -> d.getClases()
						.stream()
						.anyMatch(h -> h.getAsignatura() != null && h.getAsignatura() // Si tiene asignatura
								.getCiclo() != null && h.getAsignatura() // Si tiene ciclo
										.getCiclo()
										.getId()
										.equals(horario.getAsignatura()
												.getCiclo()
												.getId())))
				.toList()).orElse(null);
	}

	private Docente candidatosMismoDept(List<Docente> todos, Docente ausente) {
		// Seguridad ante nulls
		if (todos == null || todos.isEmpty() || ausente == null || ausente.getDepartamento() == null) {
			return null;
		}

		// Filtramos los del mismo departamento y buscamos el que tiene menos guardias
		return getConMenosGuardias(todos.stream()
				.filter(d -> d.getDepartamento() != null && d.getDepartamento()
						.getId()
						.equals(ausente.getDepartamento()
								.getId()))
				.toList()).orElse(null);
	}

	private Optional<Docente> getConMenosGuardias(List<Docente> candidatos) {
		// Seguridad ante nulls
		if (candidatos == null || candidatos.isEmpty()) {
			return Optional.empty();
		}

		// Map de Docente - Guardias realizadas, contando cuantas han cubierto
		Map<Long, Long> guardiasPorProfe = guardiaRepository.findByDocenteCubriendoIn(candidatos)
				.stream()
				.collect(Collectors.groupingBy(g -> g.getDocenteCubriendo()
						.getId(), Collectors.counting()));

		// Devolvemos el que menos guardias ha realizado, si hay empate devuelve
		// cualquiera de ellos
		return candidatos.stream()
				.min(Comparator.comparingLong(d -> guardiasPorProfe.getOrDefault(d.getId(), 0L)));
	}

	@Transactional
	public List<Guardia> generarGuardiasPorAusencia(Docente docenteAusente, LocalDate fecha) {

		int diaSemana = fecha.getDayOfWeek()
				.getValue();

		if (diaSemana == 6 || diaSemana == 7) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha " + fecha + " no es un día lectivo");
		}

		// Buscar las clases que tiene el docente ese día
		List<Horario> clases = horarioRepository.findByDocenteAndDia(docenteAusente, diaSemana);
		List<Guardia> lista = new ArrayList<>();

		for (Horario clase : clases) {
			// Buscar un sustituto para esa clase
			Docente sustituto = buscarSustituto(clase);

			Guardia g = new Guardia();
			g.setFecha(fecha);
			g.setHorario(clase);
			g.setDocenteAusente(docenteAusente);
			g.setDocenteCubriendo(sustituto);
			g.setRealizada(false);
			g.setAnotacion("Generada por Asuntos Propios");

			lista.add(g);
		}
		return lista;
	}

	@Transactional
	public Guardia generarGuardiaParaHora(FaltaDTO falta) {
		Horario horario = horarioRepository.findById(falta.getHorarioId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Horario no encontrado con id: " + falta.getHorarioId()));

		if (!horario.getDia()
				.equals(falta.getFecha()
						.getDayOfWeek()
						.getValue())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El horario no coincide con la fecha enviada");
		}

		Guardia g = new Guardia();
		Docente sustituto = buscarSustituto(horario);
		g.setFecha(falta.getFecha());
		g.setHorario(horario);
		g.setDocenteAusente(horario.getDocente());
		g.setDocenteCubriendo(sustituto);
		g.setRealizada(false);
		g.setMaterial(falta.getMaterial());
		g.setAnotacion(falta.getAnotacion());

		return g;
	}

	@Transactional
	public List<Guardia> validacionAutomatica(LocalDate fecha) {
		List<Guardia> guardias = new ArrayList<>();
		List<AsuntoPropio> solicitudes = asuntoPropioRepository.findByEstadoAndDiaSolicitado("PENDIENTE", fecha);
		solicitudes.sort(Comparator.comparing((AsuntoPropio s) -> s.getDocente()
				.getRol() != null ? s.getDocente()
						.getRol()
						.getPrioridad() : 99,
				Comparator.nullsLast(Integer::compareTo)) // Comparamos por rol
				.thenComparing(s -> s.getDocente()
						.getFechaAntiguedad(), Comparator.nullsLast(LocalDate::compareTo)) // Después por antiguedad
				.thenComparing(s -> s.getDocente()
						.getPosicion(), Comparator.nullsLast(Integer::compareTo))); // Finalmente por posicion

		int maxMismoDia = config.getAsuntos()
				.getMaxSolicitudesDia(); // Establecemos maximo de solicitudes en un mismo dia
		int contador = 0;
		int index = aux.getIndiceTrimestre(fecha);

		for (AsuntoPropio sol : solicitudes) {
			sol.setObservaciones(sol.getObservaciones() == null ? "" : sol.getObservaciones());
			if (!sol.getDocente()
					.comprobarDia(index)) { // Comprobamos si el docente tiene días disponibles
				sol.setEstado("DENEGADO");
				sol.setObservaciones(sol.getObservaciones() + " [DENEGADO POR FALTA DE DÍAS]");
				asuntoPropioRepository.save(sol);
				continue;
			}
			if (contador >= maxMismoDia) { // Comprobamos si se ha alcanzado el maximo de solicitudes en ese dia
				sol.setEstado("DENEGADO");
				sol.setObservaciones(
						sol.getObservaciones() + " [DENEGADO POR CUPO (Maximo " + maxMismoDia + " solicitudes hoy)]");
				asuntoPropioRepository.save(sol);
				continue;
			}

			try {
				// Si falla alguna guardia se cancela toda la solicitud
				List<Guardia> guardiasGeneradas = generarGuardiasPorAusencia(sol.getDocente(), sol.getDiaSolicitado());
				guardiaRepository.saveAll(guardiasGeneradas);
				// Si todo va bien actualizamos la solicitud
				guardias.addAll(guardiasGeneradas);
				sol.setEstado("ACEPTADO");
				Docente d = sol.getDocente();
				d.usarDia(index); // Restamos un día de asuntos propios al docente
				docenteRepository.save(d);
				sol.setDocente(d);
				asuntoPropioRepository.save(sol);
				contador++;
			} catch (Exception e) {
				sol.setEstado("DENEGADO");
				String mensajeError = "ERROR INTERNO DESCONOCIDO";
				if (e instanceof ResponseStatusException rse) {
					mensajeError = rse.getReason();
				} else if (e.getMessage() != null) {
					mensajeError = e.getMessage();
				}
				mensajeError = (mensajeError != null) ? mensajeError.toUpperCase() : "ERROR AL PROCESAR GUARDIAS";
				sol.setObservaciones((sol.getObservaciones() + " [" + mensajeError + "]").trim());
				asuntoPropioRepository.save(sol);
			}

		}
		return guardias;
	}

	public List<Guardia> findAll() {
		return guardiaRepository.findAll();
	}

	public Guardia findById(Long id) {
		return guardiaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se ha encontrado la guardia con id " + id));
	}

	public List<Guardia> findByDocenteCubriendoId(Long docenteId) {
		return guardiaRepository.findByDocenteCubriendoId(docenteId);
	}

	public List<Guardia> findByDocenteAusenteId(Long docenteId) {
		return guardiaRepository.findByDocenteAusenteId(docenteId);
	}

	public List<Guardia> findByFecha(LocalDate fecha) {
		return guardiaRepository.findByFecha(fecha);
	}

	public List<Guardia> findByEstado(String estado) {
		if ("REALIZADA".equalsIgnoreCase(estado)) {
			return guardiaRepository.findByRealizada(true);
		} else if ("PENDIENTE".equalsIgnoreCase(estado)) {
			return guardiaRepository.findByRealizada(false);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Estado de guardia no válido: " + estado + "(Realizada/Pendiente)");
	}

	public Guardia save(@Valid Guardia guardia) {
		return guardiaRepository.save(guardia);
	}

	@Transactional
	public void deleteById(Long id) {
		Guardia guardia = guardiaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guardia no encontrada"));

		if (guardia.getMaterial() != null && !guardia.getMaterial().isEmpty()) {
			try {
				Path rutaArchivo = Paths.get("uploads").resolve(guardia.getMaterial());
				boolean borrado = Files.deleteIfExists(rutaArchivo);
				if (borrado) {
					System.out.println("Archivo físico borrado: " + guardia.getMaterial());
				} else {
					System.out.println("El archivo no existía en disco (solo en BD), continuamos...");
				}

			} catch (IOException e) {
				System.err.println("ERROR: No se pudo borrar el archivo físico: " + e.getMessage());
			}
		}

		guardiaRepository.deleteById(id);
	}

	public List<Guardia> findByFechaAfter() {
		return guardiaRepository.findByFechaGreaterThanEqualOrderByFechaAscHorario_HoraAsc(LocalDate.now());
	}

	public List<Guardia> findByDocenteIdAndRealizada(Long docenteId, boolean b) {
		return guardiaRepository.findByDocenteCubriendoIdAndRealizada(docenteId, b);
	}

	@Transactional
	public void generarGuardiaParaAsunto(Long asuntoId) {
		AsuntoPropio asunto = asuntoPropioRepository.findById(asuntoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asunto no encontrado"));
		List<Guardia> guardiasGeneradas = generarGuardiasPorAusencia(asunto.getDocente(), asunto.getDiaSolicitado());
		guardiaRepository.saveAll(guardiasGeneradas);
	}
}