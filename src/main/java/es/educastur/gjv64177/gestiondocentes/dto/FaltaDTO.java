package es.educastur.gjv64177.gestiondocentes.dto;

import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SeguroXss
public class FaltaDTO {
	@NotNull
	private Long horarioId;
	@NotNull
	private LocalDate fecha;
	private String anotacion;
	private String material;
}
