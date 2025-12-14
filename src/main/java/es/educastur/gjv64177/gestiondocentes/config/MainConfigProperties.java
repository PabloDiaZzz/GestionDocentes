package es.educastur.gjv64177.gestiondocentes.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@PropertySource(value = "classpath:config.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "main")
@Data
public class MainConfigProperties {
	public static Integer DIAS_TRIMESTRE;

	private Asuntos asuntos = new Asuntos();

	@PostConstruct
	public void init() {
		DIAS_TRIMESTRE = this.asuntos.diasTrimestre;
		if (this.asuntos.fechasTrimestre == null || this.asuntos.fechasTrimestre.isEmpty()) {
			if (this.asuntos.cursoAcademico == null || !this.asuntos.cursoAcademico.matches("\\d{4}/\\d{4}")) {
				throw new IllegalStateException("Configuración inválida: Se requiere 'main.asuntos.fechas-trimestre' O 'main.asuntos.curso-academico' (formato YYYY/YYYY) en config.properties");
			}
			int inicioCurso = Integer.parseInt(this.asuntos.cursoAcademico.split("/")[0]);
			this.asuntos.fechasTrimestre = new ArrayList<>(List.of(LocalDate.of(inicioCurso, 12, 31), LocalDate.of(inicioCurso + 1, 3, 31)));
		}
	}

	@Data
	public static class Asuntos {
		private Integer diasTrimestre;
		private List<LocalDate> fechasTrimestre;
		private Integer maxSolicitudesDia;
		private String cursoAcademico;
	}
}
