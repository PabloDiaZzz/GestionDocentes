package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.model.AsuntoPropio;
import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.service.AsuntoPropioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/dias")
public class AsuntoPropioController {

	@Autowired
	private AsuntoPropioService asuntoPropioService;

	@GetMapping
	public List<AsuntoPropio> listarAsuntosPropios() {
		return asuntoPropioService.findAll();
	}

	@GetMapping({"/estado/{estado}"}) // pendientes aceptados denegados
	public List<AsuntoPropio> listarAsuntosPropiosPorEstado(@PathVariable String estado) {
		return asuntoPropioService.findByEstado(estado.toUpperCase());
	}

	@GetMapping("/estado/aceptados/{filtroFecha}")
	public List<AsuntoPropio> listarAsuntosPropiosAceptadosPorFecha(@PathVariable String filtroFecha) {
		return asuntoPropioService.listarAsuntosAceptadosFecha(filtroFecha);
	}

	@GetMapping("/id/{id}")
	public AsuntoPropio obtenerAsuntoPropio(@PathVariable Long id) {
		return asuntoPropioService.findById(id);
	}

	@GetMapping("/estado/{estado}/{docenteId}") // pendientes aceptados denegados
	public List<AsuntoPropio> listarAsuntosPropiosPorDocente(@PathVariable String estado, @PathVariable Long docenteId) {
		return asuntoPropioService.findByDocenteAndEstado(docenteId, estado.toUpperCase());

	}

	@GetMapping("/docente/{docenteId}")
	public List<AsuntoPropio> listarAsuntosPropiosPorDocente(@PathVariable Long docenteId) {
		return asuntoPropioService.findByDocente(docenteId);
	}

	@GetMapping("/por-disfrutar")
	public List<AsuntoPropio> docentePorDisfrutar() {
		return asuntoPropioService.diasAceptadosPorDisfrutar();
	}

	@GetMapping("/docente-top")
	public ResponseEntity<Docente> obtenerDocenteTopDiasDisfrutados() {
		return ResponseEntity.ok(asuntoPropioService.topDocenteDiasDisfrutados());
	}

	@PostMapping("/solicitar")
	public ResponseEntity<AsuntoPropio> crearAsuntoPropio(@Valid @RequestBody AsuntoPropio asuntoPropio) {
		AsuntoPropio nuevo = asuntoPropioService.solicitarDia(asuntoPropio);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}

	@PutMapping("/validar/{fecha}/{docente_id}/{estado}")
	public ResponseEntity<Boolean> actualizarAsuntoPropio(@PathVariable LocalDate fecha, @PathVariable Long docente_id, @PathVariable String estado) {
		return ResponseEntity.ok(asuntoPropioService.validarAsunto(fecha, docente_id, estado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> borrarAsuntoPropio(@PathVariable Long id) {
		asuntoPropioService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
