package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.model.RelatoGrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoGestionDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RelatoGrupoService {
    Optional<RelatoGrupo> findRelatoGrupoById(int idRelatoGrupo);

    List<RelatoGrupo> findRelatoGrupoByGrupoIs(Grupo grupo);

    void guardarRelatoGrupo(RelatoGrupo relatoGrupo);

    Optional<RelatoGrupo> findRelatoGrupoByRelatoAndGrupo(Relato relato, Grupo grupo);

    List<RelatoGrupo> buscarRelatosGrupoPorGrupoYEstado(Grupo grupo, int estado);

    void eliminarRelatoGrupo(int idrelato);

    Map<Integer, Long> contarRelatosAprobadosPorUsuarioEnGrupo(Grupo grupo);

    void gestionarRelato(RelatoGrupoGestionDto relatoGrupoGestionDto);

    RelatoGrupoDto encontrarRelatoGrupoPorId(int id);

    void actualizarRelatoGrupoEnviado(RelatoGrupo relatoGrupo, Relato relato, String firmaAutor);

    void enviarNuevoRelatoGrupo(Relato relato, Grupo grupo, String firmaAutor);

    List<RelatoGrupo> encontrarRelatosGrupoUsuario(Grupo grupo, int idUsuario) throws UsuarioException;

    List<RelatoGrupo> encontrarTodosRelatosGrupoPorUsuarioAprobados(Usuario usuario);

    boolean existenRelatosGrupoPorEstado(Grupo grupo, int estado);
}
