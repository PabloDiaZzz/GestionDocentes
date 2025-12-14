package es.educastur.gjv64177.gestiondocentes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@Table
@SeguroXss
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NombreRoles nombre;
	private Integer orden;
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "docente_id")
	@ToString.Exclude
	private Docente docente;

	public Integer getPrioridad() {
		return switch (this.nombre) {
			case ADMIN -> 0;
			case CARRERA -> 1;
			case INTERINO -> 2;
			case PRACTICAS -> 3;
		};
	}
}