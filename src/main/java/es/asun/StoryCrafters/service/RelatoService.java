package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;

import java.util.List;


public interface RelatoService {

    void guardarRelato(Relato relato, List<Integer> idCategorias);

    void actualizarRelato(int idRelato, Relato relatoActualizado, List<Integer> idCategorias);


    List<Relato> findAllRelatosByUsuario(Usuario usuario);

    Relato findRelatoById(int id);
}
