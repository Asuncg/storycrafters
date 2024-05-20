package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;

import java.util.List;
import java.util.Optional;


public interface RelatoService {

    int guardarRelato(Relato relato);

    List<Relato> findAllRelatosByUsuarioAndNotArchivado(Usuario usuario);

    Optional<Relato> findRelatoByIdAndNotArchivado(int id);

    List<Relato> findAllRelatoByUsuarioOrderByFecha(Usuario usuario);

}
