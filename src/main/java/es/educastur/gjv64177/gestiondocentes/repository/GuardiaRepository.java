package es.educastur.gjv64177.gestiondocentes.repository;

import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.model.Guardia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GuardiaRepository extends JpaRepository<Guardia, Long> {

	List<Guardia> findByDocenteCubriendoId(Long docenteId);

	List<Guardia> findByDocenteCubriendoIn(List<Docente> docentes);

	List<Guardia> findByFecha(LocalDate fecha);

	List<Guardia> findByRealizada(boolean b);
}