package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.dto.ClaseDTO;
import es.educastur.gjv64177.gestiondocentes.model.Horario;
import es.educastur.gjv64177.gestiondocentes.service.HorarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

	@Autowired
	private HorarioService horarioService;

	@GetMapping
	public List<Horario> getAllHorarios() {
		return horarioService.findAll();
	}

	@GetMapping("/{id}")
	public Horario getHorarioById(@PathVariable Long id) {
		return horarioService.findById(id);
	}

	// Endpoint extra muy útil: Horario de un profesor concreto
	@GetMapping("/docente/{docenteId}")
	public List<Horario> getHorariosByDocente(@PathVariable Long docenteId) {
		return horarioService.findByDocenteId(docenteId);
	}

	@GetMapping("/dia/{dia}")
	public List<Horario> getHorariosByDia(@PathVariable Integer dia) {
		return horarioService.findByDia(dia);
	}

	@GetMapping("/dia/{dia}/ciclo/{cicloCodigo}")
	public List<ClaseDTO> getHorariosByDiaAndCiclo(@PathVariable Integer dia, @PathVariable String cicloCodigo) {
		return horarioService.findByDiaAndCiclo(dia, cicloCodigo);
	}

	@PostMapping
	public ResponseEntity<Horario> createHorario(@Valid @RequestBody Horario horario) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(horarioService.save(horario));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Horario> updateHorario(@PathVariable Long id, @Valid @RequestBody Horario horario) {
		horarioService.findById(id); // Verifica existencia
		horario.setId(id);
		return ResponseEntity.ok(horarioService.save(horario));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteHorario(@PathVariable Long id) {
		horarioService.deleteById(id);
		return ResponseEntity.noContent()
				.build();
	}
}