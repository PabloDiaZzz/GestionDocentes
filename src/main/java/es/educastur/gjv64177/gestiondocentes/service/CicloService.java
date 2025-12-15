package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.model.Ciclo;
import es.educastur.gjv64177.gestiondocentes.repository.CicloRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CicloService {
	@Autowired
	private CicloRepository cicloRepository;

	public List<Ciclo> findAll() {
		return cicloRepository.findAll();
	}

	public Ciclo save(@Valid Ciclo ciclo) {
		return cicloRepository.save(ciclo);
	}

	public Ciclo findById(Long id) {
		return cicloRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciclo no encontrado con id: " + id));
	}

	public void deleteById(Long id) {
		cicloRepository.deleteById(id);
	}
}
