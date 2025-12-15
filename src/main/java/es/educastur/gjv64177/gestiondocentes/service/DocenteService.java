package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.config.MainConfigProperties;
import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.repository.DocenteRepository;
import es.educastur.gjv64177.gestiondocentes.repository.HorarioRepository;
import es.educastur.gjv64177.gestiondocentes.util.MetodosAux;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class DocenteService {
	@Autowired
	private DocenteRepository docenteRepository;
	@Autowired
	private HorarioRepository horarioRepository;
	@Autowired
	private MainConfigProperties config;
	@Autowired
	private MetodosAux aux;

	public List<Docente> findAll() {
		return docenteRepository.findAll();
	}

	public List<Docente> findAllSortedApellidos() {
		return docenteRepository.findAllByOrderByApellidosAscNombreAsc();
	}

	public Docente findById(Long id) {
		return docenteRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado con ID: " + id));
	}

	public Docente findBySiglas(String siglas) {
		return docenteRepository.findBySiglas(siglas).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado con ID: " + siglas));
	}

	public List<Docente> findByDepartamentoCodigo(String codigo) {
		return docenteRepository.findByDepartamentoCodigo(codigo);
	}

	public Docente save(@Valid Docente docente) {
		return docenteRepository.save(docente);
	}

	public void deleteById(Long id) {
		if (!docenteRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede borrar, no existe el ID: " + id);
		}

		if (horarioRepository.countByDocenteId(id) > 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede borrar al docente: Tiene horarios asignados");
		}

		docenteRepository.deleteById(id);
	}

	public Integer countByDepartamento(Long dept) {
		return docenteRepository.countByDepartamentoId(dept);
	}

	public Docente crearDocente(Docente docente) { // Inicializa los días del trimestre según configuración
		Integer diasPorTrimestre = config.getAsuntos().getDiasTrimestre();
		List<Integer> listaDias = IntStream.range(0, 3).map(i -> diasPorTrimestre).boxed().toList();
		docente.setDias(listaDias);
		return docenteRepository.save(docente);
	}

	public Integer comprobarDia(Docente docente, LocalDate fechaSolicitada) {
		return docente.getDias().get(aux.getIndiceTrimestre(fechaSolicitada));
	}

	@Transactional
	public boolean procesarDia(Docente docente, LocalDate fechaSolicitada) {
		List<Integer> diasDocente = docente.getDias();
		// Lógica de validación según los días disponibles del docente
		int indiceTrimestre = aux.getIndiceTrimestre(fechaSolicitada);
		if (diasDocente.get(indiceTrimestre) <= 0) {
			return false; // No hay días disponibles en este trimestre
		}
		diasDocente.set(indiceTrimestre, diasDocente.get(indiceTrimestre) - 1);
		docente.setDias(diasDocente);
		save(docente);
		return true;
	}
}
