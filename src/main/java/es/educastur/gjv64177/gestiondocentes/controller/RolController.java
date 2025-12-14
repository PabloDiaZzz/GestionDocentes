package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.model.Rol;
import es.educastur.gjv64177.gestiondocentes.service.RolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/roles")
public class RolController {

	@Autowired
	private RolService rolService;

	@GetMapping
	public List<Rol> getAllRoles() {
		return rolService.findAll();
	}

	@GetMapping("/{id}")
	public Rol getRolById(@PathVariable Long id) {
		return rolService.findById(id);
	}

	@PostMapping
	public ResponseEntity<Rol> createRol(@Valid @RequestBody Rol rol) {
		return ResponseEntity.status(HttpStatus.CREATED).body(rolService.save(rol));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Rol> updateRol(@PathVariable Long id, @Valid @RequestBody Rol rol) {
		rolService.findById(id); // Verifica existencia
		rol.setId(id);
		return ResponseEntity.ok(rolService.save(rol));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
		rolService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}