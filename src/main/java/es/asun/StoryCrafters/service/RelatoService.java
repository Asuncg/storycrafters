package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;

import java.util.List;
import java.util.Optional;


public interface RelatoService {

    int guardarRelato(Relato relato);

    List<Relato> findAllRelatosByUsuarioAndNotArchivado(Usuario usuario);

    Optional<Relato> findRelatoByIdAndNotArchivado(int id);

    List<RelatoPreviewDto> findAllRelatoByUsuarioOrderByFecha(Usuario usuario);

}
