package es.educastur.gjv64177.gestiondocentes.model;

import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table
@SeguroXss
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Horario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	private Integer dia;
	private Integer hora;
	private String aula;
	@ManyToOne
	@JoinColumn(name = "docente_id")
	private Docente docente;
	@ManyToOne
	@JoinColumn(name = "asignatura_id")
	private Asignatura asignatura;
}
