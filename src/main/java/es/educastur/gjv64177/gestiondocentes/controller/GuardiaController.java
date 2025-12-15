package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.dto.FaltaDTO;
import es.educastur.gjv64177.gestiondocentes.model.Guardia;
import es.educastur.gjv64177.gestiondocentes.service.GuardiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/guardias")
public class GuardiaController {
	@Autowired
	private GuardiaService guardiaService;

	@GetMapping
	public List<Guardia> getAllGuardias() {
		return guardiaService.findAll();
	}

	@GetMapping("/id/{id}")
	public Guardia getGuardiaById(@PathVariable Long id) {
		return guardiaService.findById(id);
	}

	@GetMapping("/docente/{docenteId}")
	public List<Guardia> getGuardiasByDocente(@PathVariable Long docenteId) {
		return guardiaService.findByDocenteId(docenteId);
	}

	@GetMapping("/fecha/{fecha}")
	public List<Guardia> getGuardiasByFecha(@PathVariable LocalDate fecha) {
		return guardiaService.findByFecha(fecha);
	}

	@GetMapping("/estado/{estado}") // PENDIENTE REALIZADA
	public List<Guardia> getGuardiasByEstado(@PathVariable String estado) {
		return guardiaService.findByEstado(estado);
	}

	@PostMapping
	public ResponseEntity<Guardia> createGuardia(@Valid @RequestBody Guardia guardia) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(guardiaService.save(guardia));
	}

	@PostMapping("/generar")
	public ResponseEntity<Guardia> generarGuardiaParaHora(@Valid @RequestBody FaltaDTO falta) {
		Guardia guardia = guardiaService.generarGuardiaParaHora(falta);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(guardiaService.save(guardia));
	}

	@PostMapping("/validacion-automatica/{fecha}")
	public ResponseEntity<List<Guardia>> validacionAutomatica(@PathVariable LocalDate fecha) {
		List<Guardia> guardiasValidadas = guardiaService.validacionAutomatica(fecha);
		return ResponseEntity.ok(guardiasValidadas);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Guardia> updateGuardia(@PathVariable Long id, @Valid @RequestBody Guardia guardia) {
		guardiaService.findById(id); // Verifica que la guardia existe
		guardia.setId(id);
		return ResponseEntity.ok(guardiaService.save(guardia));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteGuardia(@PathVariable Long id) {
		guardiaService.deleteById(id);
		return ResponseEntity.noContent()
				.build();
	}
}
