package es.educastur.gjv64177.gestiondocentes.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler { // Metodo para ocultar las traces de los errores

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handleStatusException(ResponseStatusException ex) {
		Map<String, Object> error = new HashMap<>();
		error.put("timestamp", LocalDateTime.now());
		error.put("status", ex.getStatusCode()
				.value());
		error.put("error", ex.getReason());

		return ResponseEntity.status(ex.getStatusCode())
				.body(error);
	}
}