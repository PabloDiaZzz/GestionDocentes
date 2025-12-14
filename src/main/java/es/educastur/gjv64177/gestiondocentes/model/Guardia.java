package es.educastur.gjv64177.gestiondocentes.model;

import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table
@SeguroXss
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Guardia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@Column(nullable = false)
	private LocalDate fecha;
	private String anotacion;
	private String material;
	@ManyToOne
	@JoinColumn(name = "docente_ausente_id")
	private Docente docenteAusente;
	@ManyToOne
	@JoinColumn(name = "docente_cubriendo_id")
	private Docente docenteCubriendo;
	@OneToOne
	@JoinColumn(name = "horario_id")
	private Horario horario;
	private boolean realizada;
}