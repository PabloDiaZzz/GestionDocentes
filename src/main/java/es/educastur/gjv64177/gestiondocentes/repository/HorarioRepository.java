package es.educastur.gjv64177.gestiondocentes.repository;

import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
	// Aquí pondremos consultas luego si hacen falta (ej: buscar horario de un profe)
	List<Horario> findByDocenteAndDia(Docente docenteAusente, int diaSemana);

	List<Horario> findByDiaAndHora(Integer dia, Integer hora);

	boolean existsByDocenteIdAndDiaAndHora(Long docenteId, Integer dia, Integer hora);

	Integer countByDocenteId(Long id);

	List<Horario> findByDocenteId(Long docenteId);

	List<Horario> findByDia(Integer dia);

	List<Horario> findByDiaAndAsignaturaCicloCodigo(Integer dia, String cicloCodigo);
}