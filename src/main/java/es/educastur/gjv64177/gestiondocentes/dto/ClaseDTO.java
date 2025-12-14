package es.educastur.gjv64177.gestiondocentes.dto;

import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SeguroXss
public class ClaseDTO {
	private Long horarioId;
	private String aula;
	private Integer dia;
	private Integer hora;
	private String cicloCodigo;
	private Integer curso;
	private String docenteSiglas;
}
