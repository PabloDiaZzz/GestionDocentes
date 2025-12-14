package es.educastur.gjv64177.gestiondocentes.model;

import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table
@SeguroXss
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AsuntoPropio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@ManyToOne
	@JoinColumn(name = "docente_id", nullable = false)
	private Docente docente;
	@Column(nullable = false)
	private LocalDate diaSolicitado;
	private LocalDateTime fechaPeticion;
	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'PENDIENTE'")
	private String estado;
	@Column(length = 1000)
	private String observaciones;
}