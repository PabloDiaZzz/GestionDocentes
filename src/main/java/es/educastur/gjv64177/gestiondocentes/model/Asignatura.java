package es.educastur.gjv64177.gestiondocentes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table
@SeguroXss
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Asignatura {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	private String nombre;
	private String siglas;
	private Integer curso;
	@ManyToOne
	@JoinColumn(name = "ciclo_id")
	private Ciclo ciclo;
	@OneToMany(mappedBy = "asignatura")
	@JsonIgnore
	@ToString.Exclude
	private List<Horario> horas;
}
