package es.educastur.gjv64177.gestiondocentes.repository;

import es.educastur.gjv64177.gestiondocentes.model.NombreRoles;
import es.educastur.gjv64177.gestiondocentes.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
	Optional<Rol> findByNombre(NombreRoles nombre);
}