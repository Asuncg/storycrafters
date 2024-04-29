package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.repository.CategoriaRepository;
import es.asun.StoryCrafters.repository.RelatoRepository;
import es.asun.StoryCrafters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RelatoServiceImpl implements RelatoService {

    @Autowired
    private RelatoRepository relatoRepository;

    @Override
    public int guardarRelato(Relato relato) {
        relatoRepository.save(relato);
        return relato.getId();
    }

    @Override
    public List<Relato> findAllRelatosByUsuario(Usuario usuario) {
        return relatoRepository.findByUsuario(usuario);
    }

    @Override
    public Optional<Relato> findRelatoById(int id) {
        return relatoRepository.findRelatoById(id);
    }


}
