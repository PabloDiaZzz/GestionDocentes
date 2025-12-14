package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.model.Departamento;
import es.educastur.gjv64177.gestiondocentes.repository.DepartamentoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DepartamentoService {
	@Autowired
	private DepartamentoRepository departamentoRepository;

	public List<Departamento> findAll() {
		return departamentoRepository.findAll();
	}

	public Departamento findById(Long id) {
		return departamentoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento no encontrado con ID: " + id));
	}

	public void deleteById(Long id) {
		if (!departamentoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento no encontrado con ID: " + id);
		}
		departamentoRepository.deleteById(id);
	}

	public Departamento save(@Valid Departamento departamento) {
		return departamentoRepository.save(departamento);
	}
}
