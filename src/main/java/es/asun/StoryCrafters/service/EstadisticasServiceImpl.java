package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.model.EstadisticasDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.asun.StoryCrafters.utils.Constantes.ESTADO_APROBADO;

/**
 * Implementación del servicio para calcular estadísticas de un grupo.
 */
@Service
public class EstadisticasServiceImpl implements EstadisticasService{

    @Autowired
    private RelatoGrupoService relatoGrupoService;

    /**
     * Calcula las estadísticas de un grupo dado.
     * @param grupo Grupo del que se calcularán las estadísticas.
     * @return Objeto EstadisticasDto que contiene las estadísticas calculadas.
     */
    @Override
    public EstadisticasDto calcularEstadisticasGrupo(Grupo grupo) {
        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);

        List<RelatoGrupo> relatosAprobados = listaRelatosGrupo.stream()
                .filter(relato -> relato.getEstado() == ESTADO_APROBADO)
                .toList();

        int numeroUsuarios = calcularNumeroUsuarios(grupo);
        int numeroRelatosAprobados = calcularNumeroRelatosAprobados(relatosAprobados);
        Map<String, Integer> relatosPorCategoria = calcularRelatosPorCategoria(relatosAprobados);
        Map<String, Integer> relatosPorUsuario = calcularRelatosPorUsuario(relatosAprobados);
        int relatosEnviadosUltimoMes = calcularRelatosPublicadosUltimoMes(relatosAprobados);

        return new EstadisticasDto(numeroUsuarios, numeroRelatosAprobados, relatosPorCategoria, relatosPorUsuario, relatosEnviadosUltimoMes);
    }

    /**
     * Calcula el número de relatos publicados en el último mes.
     * @param listaRelatosGrupo Lista de relatos del grupo.
     * @return Número de relatos publicados en el último mes.
     */
    private int calcularRelatosPublicadosUltimoMes(List<RelatoGrupo> listaRelatosGrupo) {
        LocalDate haceUnMes = LocalDate.now().minusMonths(1);
        return (int) listaRelatosGrupo.stream()
                .filter(relatoGrupo -> relatoGrupo.getFechaPublicacion().toInstant().isAfter(haceUnMes.atStartOfDay().toInstant(ZoneOffset.UTC)))
                .count();
    }

    /**
     * Calcula la cantidad de relatos por usuario.
     * @param listaRelatosGrupo Lista de relatos del grupo.
     * @return Mapa que asigna a cada usuario la cantidad de relatos que ha publicado.
     */
    private Map<String, Integer> calcularRelatosPorUsuario(List<RelatoGrupo> listaRelatosGrupo) {
        Map<String, Integer> relatosPorUsuario = new HashMap<>();

        for (RelatoGrupo relatoGrupo : listaRelatosGrupo) {
            String nombreUsuario = relatoGrupo.getRelato().getUsuario().getFirstName();
            relatosPorUsuario.put(nombreUsuario, relatosPorUsuario.getOrDefault(nombreUsuario, 0) + 1);
        }

        return relatosPorUsuario;
    }

    /**
     * Calcula la cantidad de relatos por categoría.
     * @param listaRelatosGrupo Lista de relatos del grupo.
     * @return Mapa que asigna a cada categoría la cantidad de relatos que contiene.
     */
    private Map<String, Integer> calcularRelatosPorCategoria(List<RelatoGrupo> listaRelatosGrupo) {
        Map<String, Integer> relatosPorCategoria = new HashMap<>();

        for (RelatoGrupo relatoGrupo : listaRelatosGrupo) {
            List<Categoria> categorias = relatoGrupo.getCategorias();
            for (Categoria categoria : categorias) {
                String nombreCategoria = categoria.getNombre();
                relatosPorCategoria.put(nombreCategoria, relatosPorCategoria.getOrDefault(nombreCategoria, 0) + 1);
            }
        }
        return relatosPorCategoria;
    }

    /**
     * Calcula el número de relatos aprobados.
     * @param listaRelatosGrupo Lista de relatos del grupo.
     * @return Número de relatos aprobados.
     */
    private int calcularNumeroRelatosAprobados(List<RelatoGrupo> listaRelatosGrupo) {
        return (int) listaRelatosGrupo.stream()
                .filter(relatoGrupo -> relatoGrupo.getEstado() == ESTADO_APROBADO)
                .count();
    }

    /**
     * Calcula el número de usuarios en un grupo.
     * @param grupo Grupo del que se calculará el número de usuarios.
     * @return Número de usuarios en el grupo.
     */
    private int calcularNumeroUsuarios(Grupo grupo) {
        return grupo.getUsuarios().size();
    }
}
