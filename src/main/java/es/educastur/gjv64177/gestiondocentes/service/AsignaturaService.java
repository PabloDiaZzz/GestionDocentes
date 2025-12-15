package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.model.Asignatura;
import es.educastur.gjv64177.gestiondocentes.repository.AsignaturaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AsignaturaService {
	@Autowired
	private AsignaturaRepository asignaturaRepository;

	public List<Asignatura> findAll() {
		return asignaturaRepository.findAll();
	}

	public Asignatura findById(Long id) {
		return asignaturaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignatura no encontrada con id: " + id));
	}

	public List<Asignatura> findByCiclo(String ciclo) {
		return asignaturaRepository.findByCicloNombreIgnoreCase(ciclo);
	}

	public List<Asignatura> findByCicloAndCurso(String ciclo, Integer curso) {
		return asignaturaRepository.findByCicloNombreIgnoreCaseAndCurso(ciclo, curso);
	}

	public Asignatura save(@Valid Asignatura asignatura) {
		return asignaturaRepository.save(asignatura);
	}

	public void deleteById(Long id) {
		asignaturaRepository.deleteById(id);
	}
}
