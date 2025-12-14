package es.educastur.gjv64177.gestiondocentes.config;

import es.educastur.gjv64177.gestiondocentes.dto.ClaseDTO;
import es.educastur.gjv64177.gestiondocentes.dto.FaltaDTO;
import es.educastur.gjv64177.gestiondocentes.model.Guardia;
import es.educastur.gjv64177.gestiondocentes.model.Horario;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig  {
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		
		TypeMap<Guardia, FaltaDTO> guardiaMap = modelMapper.createTypeMap(Guardia.class, FaltaDTO.class);
		guardiaMap.addMapping(Guardia::getMaterial, FaltaDTO::setMaterial);
		guardiaMap.addMapping(Guardia::getAnotacion, FaltaDTO::setAnotacion);
		guardiaMap.addMapping(src -> src.getHorario().getId(), FaltaDTO::setHorarioId);
		guardiaMap.addMapping(Guardia::getFecha, FaltaDTO::setFecha);

		TypeMap<Horario, ClaseDTO> horarioMap = modelMapper.createTypeMap(Horario.class, ClaseDTO.class);
		horarioMap.addMapping(Horario::getId, ClaseDTO::setHorarioId);
		horarioMap.addMapping(Horario::getAula, ClaseDTO::setAula);
		horarioMap.addMapping(Horario::getDia, ClaseDTO::setDia);
		horarioMap.addMapping(Horario::getHora, ClaseDTO::setHora);
		horarioMap.addMapping(src -> src.getAsignatura().getCiclo().getCodigo(), ClaseDTO::setCicloCodigo);
		horarioMap.addMapping(src -> src.getAsignatura().getCurso(), ClaseDTO::setCurso);
		horarioMap.addMapping(scr -> scr.getDocente().getSiglas(), ClaseDTO::setDocenteSiglas);
		
		return modelMapper;
	}
}
