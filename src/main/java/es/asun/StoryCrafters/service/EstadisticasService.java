package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.model.EstadisticasDto;

public interface EstadisticasService {
    EstadisticasDto calcularEstadisticasGrupo(Grupo grupo);
}
