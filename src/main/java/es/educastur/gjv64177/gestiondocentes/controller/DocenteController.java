package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.service.DocenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/docentes")
public class DocenteController {
	@Autowired
	private DocenteService docenteService;

	@GetMapping
	public List<Docente> listarDocentes() {
		return docenteService.findAll();
	}

	@GetMapping("/sort")
	public List<Docente> listarDocentesOrdenados() {
		return docenteService.findAllSortedApellidos();
	}

	@GetMapping("/{id}")
	public Docente obtenerDocente(@PathVariable Long id) {
		return docenteService.findById(id);
	}

	@GetMapping("/dept/{departamento}")
	public List<Docente> obtenerDocentesPorDepartamento(@PathVariable String departamento) {
		return docenteService.findByDepartamentoCodigo(departamento);
	}

	@GetMapping("/count/dept/{dept}")
	public Integer contarPorDept(@PathVariable Long dept) {
		return docenteService.countByDepartamento(dept);
	}

	@PostMapping
	public ResponseEntity<Docente> crearDocente(@Valid @RequestBody Docente docente) {
		Docente nuevo = docenteService.save(docenteService.crearDocente(docente));
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Docente> actualizarDocente(@PathVariable Long id, @Valid @RequestBody Docente docente) {
		docenteService.findById(id);
		docente.setId(id);
		return ResponseEntity.ok(docenteService.save(docente));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> borrarDocente(@PathVariable Long id) {
		docenteService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
