package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.CategoriaNotFoundException;
import es.asun.StoryCrafters.exceptions.RelatoException;
import es.asun.StoryCrafters.exceptions.UnauthorizedAccessException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;
import es.asun.StoryCrafters.repository.RelatoRepository;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static es.asun.StoryCrafters.utils.Constantes.ERROR_VIEW;
/**
 * Implementación del servicio de gestión de Relatos.
 */
@Service
public class RelatoServiceImpl implements RelatoService {

    @Autowired
    private RelatoRepository relatoRepository;

    @Autowired
    private ImagenesService imagenesService;

    @Autowired
    CategoriaService categoriaService;

    @Autowired
    private UserService userService;

    /**
     * Guarda un nuevo relato en la base de datos.
     * @param relatoDto El DTO del relato que se va a guardar.
     * @return El ID del relato guardado.
     * @throws CategoriaNotFoundException Si no se encuentra la categoría asociada al relato.
     */
    @Override
    public int guardarNuevoRelato(RelatoDto relatoDto) throws CategoriaNotFoundException, UsuarioException {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Imagen imagen = imagenesService.findImageById(relatoDto.getIdImagen());

        List<Categoria> listaCategorias = categoriaService.encontrarCategoriasPorRelato(relatoDto);

        Relato relato;

        relato = Mappings.mapToRelato(relatoDto, imagen);

        //relato.setFirmaAutor(usuario.getFirmaAutor());
        relato.setUsuario(usuario);
        relato.setCategorias(listaCategorias);
        relato.setFechaCreacion(new Date());

        relatoRepository.save(relato);

        return relato.getId();
    }

    /**
     * Encuentra todos los relatos de un usuario que no estén archivados.
     * @param usuario El usuario del cual se quieren buscar los relatos.
     * @return Una lista de relatos del usuario que no están archivados.
     */
    @Override
    public List<Relato> findAllRelatosByUsuarioAndNotArchivado(Usuario usuario) {
        return relatoRepository.findByUsuarioAndArchivadoFalse(usuario);
    }

    /**
     * Encuentra un relato por su ID que no esté archivado.
     * @param id El ID del relato a encontrar.
     * @return El relato encontrado.
     * @throws RelatoException Si el relato no se encuentra.
     */
    @Override
    public Relato findRelatoByIdAndNotArchivado(int id) throws RelatoException {
        Optional<Relato> relatoOptional = relatoRepository.findRelatoByIdAndNotArchivado(id);

        if (relatoOptional.isEmpty()) {
            throw new RelatoException("Relato no encontrado");
        }
        return relatoOptional.get();
    }

    /**
     * Encuentra todos los relatos de un usuario ordenados por fecha de actualización.
     * @param usuario El usuario del cual se quieren buscar los relatos.
     * @return Una lista de los relatos del usuario ordenados por fecha de actualización.
     */
    @Override
    public List<RelatoPreviewDto> findAllRelatoByUsuarioOrderByFecha(Usuario usuario) {
        List<Relato> relatos = relatoRepository.findByUsuarioAndArchivadoFalseOrderByFechaActualizacionDesc(usuario);

        List<RelatoPreviewDto> relatosDto = new ArrayList<>();

        for (Relato relato : relatos) {
            RelatoPreviewDto relatoDto = Mappings.mapToRelatoVistaDto(relato);
            relatosDto.add(relatoDto);
        }
        return relatosDto;
    }

    /**
     * Actualiza un relato existente en la base de datos.
     * @param relatoDto El DTO del relato actualizado.
     * @throws CategoriaNotFoundException Si no se encuentra la categoría asociada al relato.
     * @throws RelatoException Si el relato no se encuentra o el usuario no tiene permisos para actualizarlo.
     */
    @Override
    public void actualizarRelato(RelatoDto relatoDto) throws CategoriaNotFoundException, RelatoException, UsuarioException {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Relato relato = this.findRelatoByIdAndNotArchivado(relatoDto.getId());

        if (relato.getUsuario().getId() != usuario.getId()) {
            throw new UnauthorizedAccessException("No tienes permisos");
        }

        List<Categoria> listaCategorias = categoriaService.encontrarCategoriasPorRelato(relatoDto);

        //relato.setFirmaAutor(usuario.getFirmaAutor());
        relato.setTitulo(relatoDto.getTitulo());
        relato.setTexto(relatoDto.getTexto());
        relato.setFechaActualizacion(new Date());
        relato.setCategorias(listaCategorias);

        relatoRepository.save(relato);
    }

    /**
     * Archiva un relato existente en la base de datos.
     * @param id El ID del relato a archivar.
     * @throws RelatoException Si el relato no se encuentra o el usuario no tiene permisos para archivarlo.
     */
    @Override
    public void archivarRelato(int id) throws RelatoException, UsuarioException {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Relato relato = this.findRelatoByIdAndNotArchivado(id);

        if (usuario.getId() != relato.getUsuario().getId()) {
            throw new UnauthorizedAccessException("No tienes permisos");
        }

        relato.setArchivado(true);

        relatoRepository.save(relato);
    }
}