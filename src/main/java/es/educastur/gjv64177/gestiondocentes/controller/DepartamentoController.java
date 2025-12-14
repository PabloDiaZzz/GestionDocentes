package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.model.Departamento;
import es.educastur.gjv64177.gestiondocentes.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

	@Autowired
	private DepartamentoService departamentoService;

	@GetMapping
	public List<Departamento> getAllDepartamentos() {
		return departamentoService.findAll();
	}

	@GetMapping("/{id}")
	public Departamento getDepartamentoById(@PathVariable Long id) {
		return departamentoService.findById(id);
	}

	@PostMapping
	public ResponseEntity<Departamento> createDepartamento(@Valid @RequestBody Departamento departamento) {
		return ResponseEntity.status(HttpStatus.CREATED).body(departamentoService.save(departamento));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Departamento> updateDepartamento(@PathVariable Long id, @Valid @RequestBody Departamento departamento) {
		departamentoService.findById(id); // Verifica existencia
		departamento.setId(id);
		return ResponseEntity.ok(departamentoService.save(departamento));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartamento(@PathVariable Long id) {
		departamentoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
