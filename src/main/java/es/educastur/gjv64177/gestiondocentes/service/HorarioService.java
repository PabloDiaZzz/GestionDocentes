package es.educastur.gjv64177.gestiondocentes.service;

import es.educastur.gjv64177.gestiondocentes.dto.ClaseDTO;
import es.educastur.gjv64177.gestiondocentes.model.Docente;
import es.educastur.gjv64177.gestiondocentes.model.Horario;
import es.educastur.gjv64177.gestiondocentes.repository.HorarioRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HorarioService {
	@Autowired
	private HorarioRepository horarioRepository;
	@Autowired
	private ModelMapper mapper;

	public List<Horario> findAll() {
		return horarioRepository.findAll();
	}

	public Horario findById(Long id) {
		return horarioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado con ID: " + id));
	}

	public List<Horario> findByDocenteAndDia(Docente docenteAusente, int diaSemana) {
		return horarioRepository.findByDocenteAndDia(docenteAusente, diaSemana);
	}

	public List<Horario> findByDocenteId(Long docenteId) {
		return horarioRepository.findByDocenteId(docenteId);
	}

	public List<Horario> findByDia(Integer dia) {
		return horarioRepository.findByDia(dia);
	}

	public Horario save(@Valid Horario horario) {
		return horarioRepository.save(horario);
	}

	public void deleteById(Long id) {
		if (!horarioRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado con ID: " + id);
		}
		horarioRepository.deleteById(id);
	}

	public List<ClaseDTO> findByDiaAndCiclo(Integer dia, String cicloCodigo) {
		return horarioRepository.findByDiaAndAsignaturaCicloCodigo(dia, cicloCodigo)
				.stream()
				.map(h -> mapper.map(h, ClaseDTO.class))
				.toList();
	}
}
