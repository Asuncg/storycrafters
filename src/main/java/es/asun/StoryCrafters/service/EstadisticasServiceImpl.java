package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.model.EstadisticasDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstadisticasServiceImpl implements EstadisticasService{

    @Override
    public EstadisticasDto calcularEstadisticasGrupo(Grupo grupo, List<RelatoGrupo> listaRelatosGrupo) {
        int numeroUsuarios = calcularNumeroUsuarios(grupo);
        int numeroRelatos = calcularNumeroRelatosAprobados(listaRelatosGrupo);
        Map<String, Integer> relatosPorCategoria = calcularRelatosPorCategoria(listaRelatosGrupo);
        Map<String, Integer> relatosPorUsuario = calcularRelatosPorUsuario(listaRelatosGrupo);
        int relatosEnviadosUltimoMes = calcularRelatosPublicadosUltimoMes(listaRelatosGrupo);

        return new EstadisticasDto(numeroUsuarios, numeroRelatos, relatosPorCategoria, relatosPorUsuario, relatosEnviadosUltimoMes);
    }

    private int calcularRelatosPublicadosUltimoMes(List<RelatoGrupo> listaRelatosGrupo) {
        LocalDate haceUnMes = LocalDate.now().minusMonths(1);

        return (int) listaRelatosGrupo.stream()
                .filter(relatoGrupo -> relatoGrupo.getFechaPublicacion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(haceUnMes))
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
        return listaRelatosGrupo.size();
    }

    private int calcularNumeroUsuarios(Grupo grupo) {
        return grupo.getUsuarios().size();
    }
}
