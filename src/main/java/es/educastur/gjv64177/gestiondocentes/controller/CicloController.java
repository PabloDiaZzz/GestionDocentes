package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.model.Ciclo;
import es.educastur.gjv64177.gestiondocentes.service.CicloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/ciclos")
class CicloController {
	@Autowired
	private CicloService cicloService;

	@GetMapping
	public List<Ciclo> getAllCiclos() {
		return cicloService.findAll();
	}

	@GetMapping("/{id}")
	public Ciclo getCicloById(@PathVariable Long id) {
		return cicloService.findById(id);
	}

	@PostMapping
	public ResponseEntity<Ciclo> createCiclo(@Valid @RequestBody Ciclo ciclo) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(cicloService.save(ciclo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Ciclo> updateCiclo(@PathVariable Long id, @Valid @RequestBody Ciclo ciclo) {
		Ciclo updatedCiclo = cicloService.findById(id);
		updatedCiclo.setId(id);
		return ResponseEntity.ok(cicloService.save(updatedCiclo));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCiclo(@PathVariable Long id) {
		cicloService.deleteById(id);
		return ResponseEntity.ok()
				.build();
	}
}
