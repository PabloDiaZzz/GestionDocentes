package es.educastur.gjv64177.gestiondocentes.repository;

import es.educastur.gjv64177.gestiondocentes.model.AsuntoPropio;
import es.educastur.gjv64177.gestiondocentes.model.Docente;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsuntoPropioRepository extends JpaRepository<AsuntoPropio, Long> {

	List<AsuntoPropio> findByDocenteOrderByDiaSolicitadoDesc(Docente docente);

	List<AsuntoPropio> findByEstado(String estado);

	List<AsuntoPropio> findByDocenteIdAndEstado(Long docenteId, String estado);

	Optional<AsuntoPropio> findByDocenteIdAndDiaSolicitado(Long docenteId, LocalDate fecha);

	List<AsuntoPropio> findByDocenteIdAndEstadoAndDiaSolicitadoBefore(Long docenteId, String estado, LocalDate fecha);

	List<AsuntoPropio> findByDocenteIdAndEstadoAndDiaSolicitadoAfter(Long docenteId, String estado, LocalDate fecha);

	List<AsuntoPropio> findByDocenteId(Long docenteId);

	List<AsuntoPropio> findByEstadoAndDiaSolicitadoBefore(String estado, LocalDate fecha);

	List<AsuntoPropio> findByEstadoAndDiaSolicitadoAfter(String estado, LocalDate fecha);

	List<AsuntoPropio> findByEstadoAndDiaSolicitado(String estado, LocalDate fecha);

	@Query("SELECT a.docente FROM AsuntoPropio a WHERE a.estado = :estado AND a.diaSolicitado < :fecha GROUP BY a.docente ORDER BY COUNT(a) DESC")
	List<Docente> findTopDocentes(@Param("estado") String estado, @Param("fecha") LocalDate fecha, Pageable pag);

	boolean existsByDocenteIdAndDiaSolicitado(Long id, LocalDate diaSolicitado);
}