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
import java.util.stream.Collectors;

import static es.asun.StoryCrafters.utils.Constantes.ESTADO_APROBADO;

@Service
public class EstadisticasServiceImpl implements EstadisticasService{

    @Autowired
    private RelatoGrupoService relatoGrupoService;
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

    private int calcularRelatosPublicadosUltimoMes(List<RelatoGrupo> listaRelatosGrupo) {
        LocalDate haceUnMes = LocalDate.now().minusMonths(1);
        return (int) listaRelatosGrupo.stream()
                .filter(relatoGrupo -> relatoGrupo.getFechaPublicacion().toInstant().isAfter(haceUnMes.atStartOfDay().toInstant(ZoneOffset.UTC)))
                .count();
    }

    private Map<String, Integer> calcularRelatosPorUsuario(List<RelatoGrupo> listaRelatosGrupo) {
        Map<String, Integer> relatosPorUsuario = new HashMap<>();

        for (RelatoGrupo relatoGrupo : listaRelatosGrupo) {
            String nombreUsuario = relatoGrupo.getRelato().getUsuario().getFirstName();
            relatosPorUsuario.put(nombreUsuario, relatosPorUsuario.getOrDefault(nombreUsuario, 0) + 1);
        }

        return relatosPorUsuario;
    }

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

    private int calcularNumeroRelatosAprobados(List<RelatoGrupo> listaRelatosGrupo) {
        return (int) listaRelatosGrupo.stream()
                .filter(relatoGrupo -> relatoGrupo.getEstado() == ESTADO_APROBADO)
                .count();
    }

    private int calcularNumeroUsuarios(Grupo grupo) {
        return grupo.getUsuarios().size();
    }
}
