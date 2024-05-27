package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.CategoriaNotFoundException;
import es.asun.StoryCrafters.exceptions.RelatoException;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;

import java.util.List;


public interface RelatoService {

    int guardarNuevoRelato(RelatoDto relatoDto) throws CategoriaNotFoundException;

    List<Relato> findAllRelatosByUsuarioAndNotArchivado(Usuario usuario);

    Relato findRelatoByIdAndNotArchivado(int id) throws RelatoException;

    List<RelatoPreviewDto> findAllRelatoByUsuarioOrderByFecha(Usuario usuario);

    void actualizarRelato(RelatoDto relatoDto) throws CategoriaNotFoundException, RelatoException;

    void archivarRelato(int id) throws RelatoException;

}
