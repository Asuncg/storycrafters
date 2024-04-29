package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

        return null;
    }
}
