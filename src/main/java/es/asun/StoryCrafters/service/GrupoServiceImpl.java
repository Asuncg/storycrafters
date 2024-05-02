package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoServiceImpl  implements GrupoService{

    @Autowired
    private GrupoRepository grupoRepository;
    @Override
    public void guardarGrupo(Grupo grupo) {
        grupoRepository.save(grupo);
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
    public Optional<Grupo> findGrupoById(int grupoId) {
        return grupoRepository.findById(grupoId);
    }

    @Override
    public Optional<Grupo> findGrupoByCodigoAcceso(String codigoAcceso) {
        return grupoRepository.findByCodigoAcceso(codigoAcceso);
    }
}
