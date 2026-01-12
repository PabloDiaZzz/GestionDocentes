package es.educastur.gjv64177.gestiondocentes.controller;

import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.model.Guardia;
import es.educastur.gjv64177.gestiondocentes.model.Horario;
import es.educastur.gjv64177.gestiondocentes.service.GuardiaService;
import es.educastur.gjv64177.gestiondocentes.service.HorarioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SuppressWarnings("JvmTaintAnalysis")
@RestController
@RequestMapping("/api/guardias")
public class GuardiaController {
	@Autowired
	private GuardiaService guardiaService;
	private final Path CARPETA_UPLOADS = Paths.get("uploads");
	@Autowired
	private HorarioService horarioService;

	@GetMapping
	public List<Guardia> getAllGuardias() {
		return guardiaService.findAll();
	}

	@GetMapping("/id/{id}")
	public Guardia getGuardiaById(@PathVariable Long id) {
		return guardiaService.findById(id);
	}

	@GetMapping("/docente-cubriendo/{docenteId}")
	public List<Guardia> getGuardiasByDocenteCubriendo(@PathVariable Long docenteId) {
		return guardiaService.findByDocenteCubriendoId(docenteId);
	}

	@GetMapping("/docente-ausente/{docenteId}")
	public List<Guardia> getGuardiasByDocenteAusente(@PathVariable Long docenteId) {
		return guardiaService.findByDocenteAusenteId(docenteId);
	}

	@GetMapping("/fecha/{fecha}")
	public List<Guardia> getGuardiasByFecha(@PathVariable LocalDate fecha) {
		return guardiaService.findByFecha(fecha);
	}

	@GetMapping("/hoy-adelante")
	public List<Guardia> getGuardiasHoyAdelante() {
		return guardiaService.findByFechaAfter();
	}

	@GetMapping("/estado/{estado}") // PENDIENTE REALIZADA
	public List<Guardia> getGuardiasByEstado(@PathVariable String estado) {
		return guardiaService.findByEstado(estado);
	}

	@GetMapping("/{id}/material")
	public ResponseEntity<Resource> verArchivo(@PathVariable Long id) {
		try {
			Guardia guardia = guardiaService.findById(id);

			// Verificamos si hay algo en 'material' y si parece un archivo
			if (guardia == null || guardia.getMaterial() == null) {
				return ResponseEntity.notFound().build();
			}

			// Recuperamos el nombre desde 'material'
			String nombreArchivo = guardia.getMaterial();

			Path rutaArchivo = CARPETA_UPLOADS.resolve(nombreArchivo);
			Resource recurso = new UrlResource(rutaArchivo.toUri());

			if (recurso.exists() && recurso.isReadable()) {
				String contentType = null;
				try {
					contentType = Files.probeContentType(rutaArchivo);
				} catch (IOException ex) {
					// Si falla, asumimos binario genérico
				}
				if (contentType == null) {
					contentType = "application/octet-stream";
				}
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION,
								"attachment; filename=\"" + recurso.getFilename() + "\"")
						.body(recurso);
			} else {
				return ResponseEntity.notFound().build();
			}

		} catch (MalformedURLException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/realizadas/{docenteId}")
	public List<Guardia> getGuardiasRealizadas(@PathVariable Long docenteId) {
		return guardiaService.findByDocenteIdAndRealizada(docenteId, true);
	}

	@PostMapping
	public ResponseEntity<Guardia> createGuardia(@Valid @RequestBody Guardia guardia) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(guardiaService.save(guardia));
	}

	@PostMapping("/generar")
	public ResponseEntity<?> generarGuardia(
			@RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			@RequestParam("horarioId") Long horarioId,
			@RequestParam("anotacion") String anotacion,
			@RequestParam(value = "archivo", required = false) MultipartFile archivo) {
		try {
			Horario horarioAusente = horarioService.findById(horarioId);

			Guardia nuevaGuardia = new Guardia();
			nuevaGuardia.setFecha(fecha);
			nuevaGuardia.setHorario(horarioAusente);
			nuevaGuardia.setDocenteAusente(horarioAusente.getDocente());
			nuevaGuardia.setAnotacion(anotacion);
			nuevaGuardia.setRealizada(false);

			try {
				Docente sustituto = guardiaService.buscarSustituto(horarioAusente);

				nuevaGuardia.setDocenteCubriendo(sustituto);

				if (anotacion == null || anotacion.isBlank()) {
					nuevaGuardia.setAnotacion("Sustitución asignada a: " + sustituto.getNombre());
				}

			} catch (Exception e) {
				nuevaGuardia.setDocenteCubriendo(null);
				nuevaGuardia.setAnotacion(anotacion + " [SIN SUSTITUTO AUTOMÁTICO DISPONIBLE]");
			}

			if (archivo != null && !archivo.isEmpty()) {
				Path carpetaUploads = Paths.get("uploads");
				if (!Files.exists(carpetaUploads)) {
					Files.createDirectories(carpetaUploads);
				}

				String nombreArchivo = "guardia_" + System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
				Path ruta = carpetaUploads.resolve(nombreArchivo);
				Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

				nuevaGuardia.setMaterial(nombreArchivo);
			}

			guardiaService.save(nuevaGuardia);
			return ResponseEntity.ok("Guardia generada correctamente.");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}

	@PostMapping("/generar-para-asunto/{asuntoId}")
	public ResponseEntity<?> generarGuardiaParaAsunto(@PathVariable Long asuntoId) {
		try {
			guardiaService.generarGuardiaParaAsunto(asuntoId);
			return ResponseEntity.ok("Guardias generada correctamente.");
		} catch (ResponseStatusException e) {
			return ResponseEntity
					.status(e.getStatusCode())
					.body(Map.of("error", e.getReason()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity
					.internalServerError()
					.body(Map.of("error", "Ocurrió un error inesperado en el servidor."));
		}
	}

	@PostMapping("/validacion-automatica/{fecha}")
	public ResponseEntity<List<Guardia>> validacionAutomatica(@PathVariable LocalDate fecha) {
		List<Guardia> guardiasValidadas = guardiaService.validacionAutomatica(fecha);
		return ResponseEntity.ok(guardiasValidadas);
	}

	@PostMapping("/{id}/subir")
	public ResponseEntity<String> subirArchivo(@PathVariable Long id, @RequestParam("archivo") MultipartFile file) {
		try {
			if (!Files.exists(CARPETA_UPLOADS)) {
				Files.createDirectories(CARPETA_UPLOADS);
			}

			Guardia guardia = guardiaService.findById(id);
			if (guardia == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			// Generamos nombre único
			String nombreUnico = "guardia_" + id + "_" + file.getOriginalFilename();

			// Guardamos físico
			Path rutaCompleta = CARPETA_UPLOADS.resolve(nombreUnico);
			Files.copy(file.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

			// --- CAMBIO CLAVE AQUÍ ---
			// Guardamos el nombre del archivo en el campo 'material'
			guardia.setMaterial(nombreUnico);
			guardiaService.save(guardia);

			return ResponseEntity.ok("Archivo subido y vinculado al campo material.");

		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
		}
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
