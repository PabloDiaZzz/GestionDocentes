package es.educastur.gjv64177.gestiondocentes.util;

import es.educastur.gjv64177.gestiondocentes.config.MainConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MetodosAux {
	@Autowired
	private MainConfigProperties config;

	/**
	 * Método que devuelve el índice del trimestre al que pertenece una fecha dada.
	 *
	 * @param fecha La fecha a evaluar.
	 * @return El índice del trimestre (0 para 1º, 1 para 2º, 2 para 3º).
	 */
	public int getIndiceTrimestre(LocalDate fecha) {
		// Asumimos que fechasTrimestre tiene 2 fechas: [FinT1, FinT2]
		if (!fecha.isAfter(config.getAsuntos()
				                   .getFechasTrimestre()
				                   .getFirst())) {
			return 0;
		} else if (!fecha.isAfter(config.getAsuntos()
				                          .getFechasTrimestre()
				                          .getLast())) {
			return 1;
		} else {
			return 2;
		}
	}
}
