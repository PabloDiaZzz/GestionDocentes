package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.model.NombreRoles;
import es.educastur.gjv64177.gestiondocentes.model.Rol;
import es.educastur.gjv64177.gestiondocentes.repository.RolRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RolService {
	@Autowired
	private RolRepository rolRepository;

	public List<Rol> findAll() {
		return rolRepository.findAll();
	}

	public Rol findById(Long id) {
		return rolRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado con ID: " + id));
	}

	public Rol findByNombre(NombreRoles nombre) {
		return rolRepository.findByNombre(nombre).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado con nombre: " + nombre));
	}

	public void deleteById(Long id) {
		if (!rolRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado con ID: " + id);
		}
		rolRepository.deleteById(id);
	}

	public Rol save(@Valid Rol rol) {
		return rolRepository.save(rol);
	}
}
