package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.CategoriaNotFoundException;
import es.asun.StoryCrafters.exceptions.RelatoException;
import es.asun.StoryCrafters.exceptions.UnauthorizedAccessException;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;
import es.asun.StoryCrafters.repository.RelatoRepository;
import es.asun.StoryCrafters.utils.AuthUtils;
import es.asun.StoryCrafters.utils.Constantes;
import es.asun.StoryCrafters.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static es.asun.StoryCrafters.utils.Constantes.ERROR_VIEW;

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

    @Override
    public int guardarNuevoRelato(RelatoDto relatoDto) throws CategoriaNotFoundException {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Imagen imagen = imagenesService.findImageById(relatoDto.getIdImagen());

        List<Categoria> listaCategorias = categoriaService.encontrarCategoriasPorRelato(relatoDto);

        Relato relato;

        relato = Mappings.mapToRelato(relatoDto, imagen);

        relato.setUsuario(usuario);
        relato.setCategorias(listaCategorias);
        relato.setFechaCreacion(new Date());

        relatoRepository.save(relato);

        return relato.getId();
    }

    @Override
    public List<Relato> findAllRelatosByUsuarioAndNotArchivado(Usuario usuario) {
        return relatoRepository.findByUsuarioAndArchivadoFalse(usuario);
    }

    @Override
    public Relato findRelatoByIdAndNotArchivado(int id) throws RelatoException {
        Optional<Relato> relatoOptional = relatoRepository.findRelatoByIdAndNotArchivado(id);

        if (relatoOptional.isEmpty()) {
            throw new RelatoException("Relato no encontrado");
        }
        return relatoOptional.get();
    }

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

    @Override
    public int actualizarRelato(RelatoDto relatoDto) throws CategoriaNotFoundException, RelatoException {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Relato relato = this.findRelatoByIdAndNotArchivado(relatoDto.getId());

        if (relato.getUsuario().getId() != usuario.getId()) {
            throw new UnauthorizedAccessException("No tienes permisos");
        }

        List<Categoria> listaCategorias = categoriaService.encontrarCategoriasPorRelato(relatoDto);

        relato.setTitulo(relatoDto.getTitulo());
        relato.setTexto(relatoDto.getTexto());
        relato.setFechaActualizacion(new Date());
        relato.setCategorias(listaCategorias);

        relatoRepository.save(relato);

        return relato.getId();
    }

    @Override
    public void archivarRelato(int id) throws RelatoException {
        Usuario usuario = AuthUtils.getAuthUser(userService);

        Relato relato = this.findRelatoByIdAndNotArchivado(id);

        if (usuario.getId() != relato.getUsuario().getId()) {
            throw new UnauthorizedAccessException("No tienes permisos");
        }

        relato.setArchivado(true);

        relatoRepository.save(relato);
    }
}