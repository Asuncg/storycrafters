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

@Service
public class RelatoGrupoServiceImpl implements RelatoGrupoService {

    @Autowired
    private RelatoGrupoRepository relatoGrupoRepository;

    @Autowired
    private  UserService userService;

    @Override
    public Optional<RelatoGrupo> findRelatoGrupoById(int idRelatoGrupo) {

        return relatoGrupoRepository.findById(idRelatoGrupo);
    }

    @Override
    public List<RelatoGrupo> findRelatoGrupoByGrupoIs(Grupo grupo) {
        return relatoGrupoRepository.findByGrupoIs(grupo);
    }

    @Override
    public void guardarRelatoGrupo(RelatoGrupo relatoGrupo) {
        relatoGrupoRepository.save(relatoGrupo);
    }

    @Override
    public Optional<RelatoGrupo> findRelatoGrupoByRelatoAndGrupo(Relato relato, Grupo grupo) {
        return relatoGrupoRepository.findByRelatoAndGrupo(relato, grupo);
    }

    @Override
    public List<RelatoGrupo> buscarRelatosGrupo(Grupo grupo, int estado) {
        return relatoGrupoRepository.findByGrupoAndEstadoOrderByFechaPublicacionDesc(grupo, estado);
    }

    @Override
    public void eliminarRelatoGrupo(int idrelato) {
        relatoGrupoRepository.deleteById(idrelato);
    }

    @Override
    public Map<Integer, Long> contarRelatosAprobadosPorUsuarioEnGrupo(Grupo grupo) {
        List<RelatoGrupo> listaRelatosGrupo = findRelatoGrupoByGrupoIs(grupo);
        return listaRelatosGrupo.stream()
                .filter(relato -> relato.getEstado() == Constantes.ESTADO_APROBADO)
                .collect(Collectors.groupingBy(relato -> relato.getRelato().getUsuario().getId(), Collectors.counting()));
    }


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

    @Override
    public RelatoGrupoDto encontrarRelatoGrupoPorId(int id) throws RelatoGrupoException{
        Optional<RelatoGrupo> relatoGrupoOptional = this.findRelatoGrupoById(id);
        if (relatoGrupoOptional.isEmpty()) {
            throw new RelatoGrupoException("Relato de grupo no encontrado");
        }
        RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
        return Mappings.mapToRelatoGrupoDto(relatoGrupo);
    }

    @Override
    public void actualizarRelatoGrupoEnviado(RelatoGrupo relatoGrupo, Relato relato) {
        relatoGrupo.setRelato(relato);
        relatoGrupo.setTitulo(relato.getTitulo());
        relatoGrupo.setTexto(relato.getTexto());
        relatoGrupo.setFirmaAutor(relato.getFirmaAutor());
        relatoGrupo.setImagen(relato.getImagen());
        relatoGrupo.setEstado(1);
        relatoGrupo.setFeedback("");
        relatoGrupo.setFechaModificacion(new Date());
        relatoGrupo.setCategorias(asociarCategorias(relato));

        this.guardarRelatoGrupo(relatoGrupo);
    }

    @Override
    public void enviarNuevoRelatoGrupo(RelatoGrupo relatoGrupo, Relato relato, Grupo grupo) {
        relatoGrupo.setRelato(relato);
        relatoGrupo.setGrupo(grupo);
        relatoGrupo.setTitulo(relato.getTitulo());
        relatoGrupo.setTexto(relato.getTexto());
        relatoGrupo.setFirmaAutor(relato.getFirmaAutor());
        relatoGrupo.setImagen(relato.getImagen());
        relatoGrupo.setFechaModificacion(new Date());
        relatoGrupo.setCategorias(asociarCategorias(relato));

        this.guardarRelatoGrupo(relatoGrupo);
    }

    @Override
    public List<RelatoGrupo> encontrarRelatosGrupoUsuario(Grupo grupo, int idUsuario) throws UsuarioException {
        Usuario usuario = userService.findUserById(idUsuario);

        List<RelatoGrupo> listaRelatosGrupo = this.findRelatoGrupoByGrupoIs(grupo);

        return listaRelatosGrupo.stream()
                .filter(relato -> relato.getRelato().getUsuario().equals(usuario) && relato.getEstado() == ESTADO_APROBADO)
                .collect(Collectors.toList());
    }

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
