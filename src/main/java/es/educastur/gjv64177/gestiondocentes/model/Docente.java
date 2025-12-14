package es.educastur.gjv64177.gestiondocentes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.educastur.gjv64177.gestiondocentes.config.MainConfigProperties;
import es.educastur.gjv64177.gestiondocentes.util.NoLimpiar;
import es.educastur.gjv64177.gestiondocentes.util.SeguroXss;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Table
@SeguroXss
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Docente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@Column(columnDefinition = "DATE DEFAULT (CURRENT_DATE)")
	private LocalDate fechaAntiguedad = LocalDate.now();
	private String nombre;
	private String apellidos;
	@Email
	private String email;
	@Column(unique = true, nullable = false)
	private String siglas;
	private Integer posicion;
	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = "trimestre_index")
	private List<Integer> dias = new ArrayList<>();
	@OneToOne(mappedBy = "docente", cascade = CascadeType.ALL)
	private Rol rol;
	@ManyToOne
	@JoinColumn(name = "departamento_id")
	private Departamento departamento;
	@JsonIgnore
	@NoLimpiar
	@ToString.Exclude
	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'Temporal01'")
	private String password = "Temporal01";
	@JsonIgnore
	@OneToMany(mappedBy = "docente", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Horario> clases;

	public Docente() {
		if (MainConfigProperties.DIAS_TRIMESTRE != null) {
			this.dias = new ArrayList<>(Collections.nCopies(3, MainConfigProperties.DIAS_TRIMESTRE >= 0 ? MainConfigProperties.DIAS_TRIMESTRE : 0));
		}
	}

	public boolean comprobarDia(int index) {
		if (this.dias == null || this.dias.size() <= index)
			return false;
		return this.dias.get(index) > 0;
	}

	public void usarDia(int index) {
		if (!comprobarDia(index)) {
			throw new IllegalStateException("No quedan días disponibles en el trimestre " + (index + 1));
		}
		if (this.dias == null || this.dias.size() <= index)
			return;
		this.dias.set(index, this.dias.get(index) - 1);
	}
}