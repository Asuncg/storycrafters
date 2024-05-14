package es.asun.StoryCrafters.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDto {
    private int numeroUsuarios;
    private int numeroRelatosPublicados;
    private Map<String, Integer> relatosPorCategoria;
    private Map<String, Integer> relatosPorUsuario;
    private int relatosPublicadosUltimoMes;
}
