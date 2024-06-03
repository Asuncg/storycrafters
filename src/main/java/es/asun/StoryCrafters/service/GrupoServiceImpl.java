package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.exceptions.*;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.repository.GrupoRepository;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.CodigoIngresoGenerator;
import es.asun.StoryCrafters.utils.Mappings;
import es.asun.StoryCrafters.utils.Validadores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

import static es.asun.StoryCrafters.utils.Constantes.*;

@Service
public class GrupoServiceImpl implements GrupoService {

    @Autowired
    private UserService userService;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private RelatoGrupoService relatoGrupoService;

    @Override
    public void guardarGrupo(Grupo grupo) {
        grupoRepository.save(grupo);
    }

    @Override
    public void crearGrupo(GrupoDto grupoDto) {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Grupo grupo;

        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios.add(usuario);

        String codigoAcceso = CodigoIngresoGenerator.generarCodigoIngreso(this);

        grupo = Mappings.mapToGrupo(grupoDto);
        grupo.setUsuario(usuario);
        grupo.setUsuarios(listaUsuarios);
        grupo.setCodigoAcceso(codigoAcceso);
        grupoRepository.save(grupo);
    }

    @Override
    public String actualizarGrupo(String idGrupo, GrupoDto grupoDto) throws InvalidGrupoException, UnauthorizedAccessException, GrupoException {
        int id = Integer.parseInt(idGrupo);
        String redirect = "redirect:/grupos/";
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Grupo grupo = this.findGrupoById(id);

        if (!Validadores.gestorValido(grupo, usuario)) {
            throw new InvalidGrupoException("Invalid group or unauthorized access");
        }

        grupo.setNombre(grupoDto.getNombre());
        grupo.setDescripcion(grupoDto.getDescripcion());
        this.guardarGrupo(grupo);

        return redirect;
    }

    @Override
    public List<Grupo> findAllGruposByUsuario(Usuario usuario) {

        return grupoRepository.findAllByUsuario(usuario);
    }

    @Override
    public void deleteGrupoById(int idGrupo) {
        grupoRepository.deleteById(idGrupo);
    }

    @Override
    public Grupo findGrupoById(int grupoId) throws GrupoException {
        Optional<Grupo> grupoOptional = grupoRepository.findById(grupoId);
        if (grupoOptional.isEmpty()) {
            throw new GrupoException("Grupo no encontrado");
        }
        return grupoOptional.get();
    }

    @Override
    public Optional<Grupo> findGrupoByCodigoAcceso(String codigoAcceso) {
        return grupoRepository.findByCodigoAcceso(codigoAcceso);
    }

    @Override
    public void eliminarGrupo(int id) {
        this.deleteGrupoById(id);
    }

    @Override
    public void enviarInvitacion(int idGrupo, String email) throws GrupoException, UsuarioException, UserAlreadyExistsException {
        Usuario usuario = AuthUtils.getAuthUser(userService);
        Grupo grupo = this.findGrupoById(idGrupo);

        if (!Validadores.gestorValido(grupo, usuario)) {
            throw new UnauthorizedAccessException("Acceso Denegado");
        }
        Optional<Usuario> usuarioGrupoOptional = userService.findUserByEmail(email);

        if (usuarioGrupoOptional.isEmpty()){
            throw new UsuarioException("El usuario no existe");
        }
        Usuario usuarioGrupo = usuarioGrupoOptional.get();

        if (yaExisteUsuario(usuarioGrupo)) {
            throw new UserAlreadyExistsException("Este usuario ya existe en el grupo");
        }
        emailService.enviarInvitacion(email, grupo.getCodigoAcceso(), grupo.getDescripcion(), grupo.getNombre(), usuario.getFirstName());

    }

    @Override
    public void abandonarGrupo(Usuario usuario, String grupoId) throws GrupoException {
        int idGrupo = Integer.parseInt(grupoId);
        Grupo grupo = this.findGrupoById(idGrupo);

        if (!grupo.getUsuarios().contains(usuario)) {
            throw new GrupoException("El usuario no es miembro del grupo");
        }

        grupo.getUsuarios().remove(usuario);
        this.guardarGrupo(grupo);
    }

    @Override
    public void mostrarVista(Grupo grupo, String opcion, Model model, Usuario usuario) throws GrupoException {
        List<RelatoGrupo> relatosAprobados;
        switch (opcion) {
            case "publicaciones":
                List<Categoria> listaCategorias = categoriaService.findAllCategories();
                model.addAttribute("listaCategorias", listaCategorias);

                relatosAprobados = relatoGrupoService.buscarRelatosGrupo(grupo, ESTADO_APROBADO);

                model.addAttribute("listaRelatosAprobados", relatosAprobados);
                model.addAttribute("content", "views/grupos/ver-grupo");
                break;
            case "usuarios":
                Map<Integer, Long> contadorRelatosPorUsuario = relatoGrupoService.contarRelatosAprobadosPorUsuarioEnGrupo(grupo);

                model.addAttribute("contadorRelatosPorUsuario", contadorRelatosPorUsuario);
                model.addAttribute("content", "views/grupos/grupo-usuarios");
                break;
            case "gestionar-relatos":
                List<RelatoGrupo> relatosRechazados = relatoGrupoService.buscarRelatosGrupo(grupo, ESTADO_RECHAZADO);
                relatosAprobados = relatoGrupoService.buscarRelatosGrupo(grupo, ESTADO_APROBADO);

                model.addAttribute("listaRelatosAprobados", relatosAprobados);
                model.addAttribute("listaRelatosRechazados", relatosRechazados);
                model.addAttribute("content", "views/grupos/gestionar-relatos-grupo");
                break;
            case "solicitudes":
                model.addAttribute("content", "views/grupos/grupo-solicitudes");
                break;
            default:
                throw new GrupoException("Opción no válida");
        }
    }

    @Override
    public void verMisRelatosGrupo(Grupo grupo, Usuario usuarioActual, Model model) {
        List<RelatoGrupo> listaRelatosGrupo = relatoGrupoService.findRelatoGrupoByGrupoIs(grupo);

        List<RelatoGrupo> listaRelatosUsuario = listaRelatosGrupo.stream()
                .filter(relato -> relato.getRelato().getUsuario().equals(usuarioActual))
                .collect(Collectors.toList());

        model.addAttribute("listaRelatosUsuario", listaRelatosUsuario);
        model.addAttribute("content", "views/grupos/grupo-mis-relatos");
    }

    @Override
    public boolean existeCodigoAcceso(String codigoAcceso) {
        return grupoRepository.existsByCodigoAcceso(codigoAcceso);
    }

    @Override
    public boolean yaExisteUsuario(Usuario usuario) {
        return grupoRepository.existsByUsuariosContains(usuario);
    }

    @Override
    public List<Grupo> encontrarGruposContieneUsuario(Usuario usuario) {
        return grupoRepository.findByUsuariosContains(usuario);
    }

}
