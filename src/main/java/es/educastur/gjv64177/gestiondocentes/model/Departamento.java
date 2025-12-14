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
public class Departamento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	private String nombre;
	private String codigo;
	private String telefono;
	@OneToMany(mappedBy = "departamento")
	@JsonIgnore
	@ToString.Exclude
	private List<Docente> docentes;
}