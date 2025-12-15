package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.model.Asignatura;
import es.educastur.gjv64177.gestiondocentes.service.AsignaturaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/asignaturas")
class AsignaturaController {
	@Autowired
	private AsignaturaService asignaturaService;

	@GetMapping
	public List<Asignatura> listarAsignaturas() {
		return asignaturaService.findAll();
	}

	@GetMapping("/id/{id}")
	public Asignatura findById(@PathVariable Long id) {
		return asignaturaService.findById(id);
	}

	@GetMapping("/ciclo/{ciclo}")
	public List<Asignatura> findByCiclo(@PathVariable String ciclo) {
		return asignaturaService.findByCiclo(ciclo);
	}

	@GetMapping("/ciclo/{ciclo}/{curso}")
	public List<Asignatura> findByCicloAndCurso(@PathVariable String ciclo, @PathVariable Integer curso) {
		return asignaturaService.findByCicloAndCurso(ciclo, curso);
	}

	@PostMapping
	public ResponseEntity<Asignatura> crearAsignatura(@Valid @RequestBody Asignatura asignatura) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(asignaturaService.save(asignatura));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Asignatura> actualizarAsignatura(@PathVariable Long id, @Valid @RequestBody Asignatura asignatura) {
		asignaturaService.findById(id); // Verifica existencia
		asignatura.setId(id);
		return ResponseEntity.ok(asignaturaService.save(asignatura));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarAsignatura(@PathVariable Long id) {
		asignaturaService.deleteById(id);
		return ResponseEntity.noContent()
				.build();
	}
}