package es.educastur.gjv64177.gestiondocentes.repository;

import es.educastur.gjv64177.gestiondocentes.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
	Optional<Asignatura> findBySiglas(String siglas);

	List<Asignatura> findByCicloNombreIgnoreCase(String ciclo);

	List<Asignatura> findByCicloNombreIgnoreCaseAndCurso(String ciclo, Integer curso);
}
