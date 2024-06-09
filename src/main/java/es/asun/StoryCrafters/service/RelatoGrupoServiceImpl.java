package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.RelatoGrupoException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.model.RelatoGrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoGestionDto;
import es.asun.StoryCrafters.repository.RelatoGrupoRepository;
import es.asun.StoryCrafters.utils.Constantes;
import es.asun.StoryCrafters.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static es.asun.StoryCrafters.utils.Constantes.*;

/**
 * Esta clase implementa la interfaz RelatoGrupoService y proporciona métodos para gestionar los relatos dentro de un grupo.
 */
@Service
public class RelatoGrupoServiceImpl implements RelatoGrupoService {

    @Autowired
    private RelatoGrupoRepository relatoGrupoRepository;

    @Autowired
    private  UserService userService;

    /**
     * Busca un RelatoGrupo por su ID.
     * @param idRelatoGrupo El ID del RelatoGrupo a buscar.
     * @return Un objeto Optional que contiene el RelatoGrupo si se encuentra, o vacío si no se encuentra.
     */
    @Override
    public Optional<RelatoGrupo> findRelatoGrupoById(int idRelatoGrupo) {
        return relatoGrupoRepository.findById(idRelatoGrupo);
    }

    /**
     * Busca todos los RelatoGrupo asociados a un Grupo dado.
     * @param grupo El Grupo del cual se quieren buscar los RelatoGrupo.
     * @return Una lista de RelatoGrupo asociados al Grupo dado.
     */
    @Override
    public List<RelatoGrupo> findRelatoGrupoByGrupoIs(Grupo grupo) {
        return relatoGrupoRepository.findByGrupoIs(grupo);
    }

    /**
     * Guarda un RelatoGrupo en la base de datos.
     * @param relatoGrupo El RelatoGrupo a guardar.
     */
    @Override
    public void guardarRelatoGrupo(RelatoGrupo relatoGrupo) {
        relatoGrupoRepository.save(relatoGrupo);
    }

    /**
     * Busca un RelatoGrupo específico por un Relato y un Grupo dados.
     * @param relato El Relato asociado al RelatoGrupo.
     * @param grupo El Grupo asociado al RelatoGrupo.
     * @return Un objeto Optional que contiene el RelatoGrupo si se encuentra, o vacío si no se encuentra.
     */
    @Override
    public Optional<RelatoGrupo> findRelatoGrupoByRelatoAndGrupo(Relato relato, Grupo grupo) {
        return relatoGrupoRepository.findByRelatoAndGrupo(relato, grupo);
    }

    /**
     * Busca todos los RelatoGrupo de un Grupo dado con un estado específico.
     * @param grupo El Grupo del cual se quieren buscar los RelatoGrupo.
     * @param estado El estado de los RelatoGrupo a buscar.
     * @return Una lista de RelatoGrupo con el estado especificado asociados al Grupo dado.
     */
    @Override
    public List<RelatoGrupo> buscarRelatosGrupoPorGrupoYEstado(Grupo grupo, int estado) {
        return relatoGrupoRepository.findByGrupoAndEstadoOrderByFechaPublicacionDesc(grupo, estado);
    }

    /**
     * Elimina un RelatoGrupo de la base de datos.
     * @param idrelato El ID del RelatoGrupo a eliminar.
     */
    @Override
    public void eliminarRelatoGrupo(int idrelato) {
        relatoGrupoRepository.deleteById(idrelato);
    }

    /**
     * Cuenta el número de RelatoGrupo aprobados por usuario en un Grupo dado.
     * @param grupo El Grupo del cual se quiere contar los RelatoGrupo.
     * @return Un Mapa donde la clave es el ID del usuario y el valor es el número de RelatoGrupo aprobados.
     */
    @Override
    public Map<Integer, Long> contarRelatosAprobadosPorUsuarioEnGrupo(Grupo grupo) {
        List<RelatoGrupo> listaRelatosGrupo = findRelatoGrupoByGrupoIs(grupo);
        return listaRelatosGrupo.stream()
                .filter(relato -> relato.getEstado() == Constantes.ESTADO_APROBADO)
                .collect(Collectors.groupingBy(relato -> relato.getRelato().getUsuario().getId(), Collectors.counting()));
    }

    /**
     * Gestiona un RelatoGrupo cambiando su estado, calificación y feedback.
     * @param relatoGrupoGestionDto El DTO que contiene la información necesaria para gestionar el RelatoGrupo.
     * @throws RelatoGrupoException Si el RelatoGrupo no se encuentra.
     */
    @Override
    public void gestionarRelato(RelatoGrupoGestionDto relatoGrupoGestionDto) throws RelatoGrupoException {
        int idRelatoGrupo = relatoGrupoGestionDto.getId();

        Optional<RelatoGrupo> relatoGrupoOptional = this.findRelatoGrupoById(idRelatoGrupo);

        if (relatoGrupoOptional.isPresent()) {
            RelatoGrupo relatoGrupo = relatoGrupoOptional.get();

            if (relatoGrupoGestionDto.isAprobado()) {
                relatoGrupo.setEstado(ESTADO_APROBADO);
            } else {
                relatoGrupo.setEstado(ESTADO_RECHAZADO);
            }

            relatoGrupo.setCalificacion(relatoGrupoGestionDto.getCalificacion());
            relatoGrupo.setFeedback(relatoGrupoGestionDto.getFeedback());
            relatoGrupo.setFechaModificacion(new Date());
            this.guardarRelatoGrupo(relatoGrupo);
        } else {
            throw new RelatoGrupoException("Relato de grupo no encontrado");
        }
    }

    /**
     * Encuentra un RelatoGrupo por su ID y lo convierte en un RelatoGrupoDto.
     * @param id El ID del RelatoGrupo a encontrar.
     * @return Un objeto RelatoGrupoDto que representa el RelatoGrupo encontrado.
     * @throws RelatoGrupoException Si el RelatoGrupo no se encuentra.
     */
    @Override
    public RelatoGrupoDto encontrarRelatoGrupoPorId(int id) throws RelatoGrupoException{
        Optional<RelatoGrupo> relatoGrupoOptional = this.findRelatoGrupoById(id);
        if (relatoGrupoOptional.isEmpty()) {
            throw new RelatoGrupoException("Relato de grupo no encontrado");
        }
        RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
        return Mappings.mapToRelatoGrupoDto(relatoGrupo);
    }

    /**
     * Actualiza un RelatoGrupo con la información de un Relato dado.
     *
     * @param relatoGrupo El RelatoGrupo a actualizar.
     * @param relato      El Relato con la información actualizada.
     * @param firmaAutor
     */
    @Override
    public void actualizarRelatoGrupoEnviado(RelatoGrupo relatoGrupo, Relato relato, String firmaAutor) {
        relatoGrupo.setRelato(relato);
        relatoGrupo.setTitulo(relato.getTitulo());
        relatoGrupo.setTexto(relato.getTexto());
        relatoGrupo.setFirmaAutor(firmaAutor);
        relatoGrupo.setImagen(relato.getImagen());
        relatoGrupo.setEstado(1);
        relatoGrupo.setFeedback("");
        relatoGrupo.setFechaModificacion(new Date());
        relatoGrupo.setCategorias(asociarCategorias(relato));

        this.guardarRelatoGrupo(relatoGrupo);
    }

    /**
     * Envía un nuevo RelatoGrupo asociado a un Grupo dado.
     *
     * @param relato     El Relato asociado al RelatoGrupo.
     * @param grupo      El Grupo asociado al RelatoGrupo.
     * @param firmaAutor
     */
    @Override
    public void enviarNuevoRelatoGrupo(Relato relato, Grupo grupo, String firmaAutor) {
        RelatoGrupo relatoGrupo = new RelatoGrupo();
        relatoGrupo.setRelato(relato);
        relatoGrupo.setGrupo(grupo);
        relatoGrupo.setTitulo(relato.getTitulo());
        relatoGrupo.setTexto(relato.getTexto());
        relatoGrupo.setFirmaAutor(firmaAutor);
        relatoGrupo.setImagen(relato.getImagen());
        relatoGrupo.setFechaModificacion(new Date());
        relatoGrupo.setCategorias(asociarCategorias(relato));

        this.guardarRelatoGrupo(relatoGrupo);
    }

    /**
     * Encuentra todos los RelatoGrupo de un Usuario dentro de un Grupo que estén aprobados.
     * @param grupo El Grupo del cual se quieren buscar los RelatoGrupo.
     * @param idUsuario El ID del Usuario del cual se quieren buscar los RelatoGrupo.
     * @return Una lista de RelatoGrupo aprobados del Usuario dentro del Grupo.
     * @throws UsuarioException Si el Usuario no se encuentra.
     */
    @Override
    public List<RelatoGrupo> encontrarRelatosGrupoUsuario(Grupo grupo, int idUsuario) throws UsuarioException {
        Usuario usuario = userService.findUserById(idUsuario);

        List<RelatoGrupo> listaRelatosGrupo = this.findRelatoGrupoByGrupoIs(grupo);

        return listaRelatosGrupo.stream()
                .filter(relato -> relato.getRelato().getUsuario().equals(usuario) && relato.getEstado() == ESTADO_APROBADO)
                .collect(Collectors.toList());
    }

    /**
     * Encuentra todos los RelatoGrupo asociados a un Usuario que estén aprobados.
     * @param usuario El Usuario del cual se quieren buscar los RelatoGrupo.
     * @return Una lista de RelatoGrupo aprobados asociados al Usuario.
     */
    @Override
    public List<RelatoGrupo> encontrarTodosRelatosGrupoPorUsuarioAprobados(Usuario usuario) {
        return relatoGrupoRepository.findByRelato_UsuarioAndEstadoEquals(usuario, ESTADO_APROBADO);
    }

    /**
     * Verifica si existen RelatoGrupo en un Grupo con un estado específico.
     * @param grupo El Grupo en el cual se quiere verificar la existencia de RelatoGrupo.
     * @param estado El estado de los RelatoGrupo a verificar.
     * @return true si existen RelatoGrupo con el estado especificado en el Grupo, false en caso contrario.
     */
    @Override
    public boolean existenRelatosGrupoPorEstado(Grupo grupo, int estado) {
        return relatoGrupoRepository.existsRelatoGrupoByGrupoAndEstado(grupo, estado);
    }

    /**
     * Asocia las categorías de un Relato a un nuevo RelatoGrupo.
     * @param relato El Relato del cual se obtienen las categorías.
     * @return Una lista de categorías asociadas al nuevo RelatoGrupo.
     */
    private List<Categoria> asociarCategorias(Relato relato) {
        List<Categoria> categorias = new ArrayList<>();
        for (Categoria categoria : relato.getCategorias()) {
            Categoria nuevaCategoria = new Categoria();
            nuevaCategoria.setId(categoria.getId());
            nuevaCategoria.setNombre(categoria.getNombre());
            categorias.add(nuevaCategoria);
        }
        return categorias;
    }

}
