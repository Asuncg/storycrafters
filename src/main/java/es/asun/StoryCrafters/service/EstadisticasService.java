package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.model.EstadisticasDto;

import java.util.List;

public interface EstadisticasService {
    EstadisticasDto calcularEstadisticasGrupo(Grupo grupo);
}
