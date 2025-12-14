package es.educastur.gjv64177.gestiondocentes.repository;

import es.educastur.gjv64177.gestiondocentes.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

	Optional<Docente> findBySiglas(String siglas);

	List<Docente> findByDepartamentoCodigo(String codigo);

	List<Docente> findAllByOrderByApellidosAscNombreAsc();

	Integer countByDepartamentoId(Long dept);
}