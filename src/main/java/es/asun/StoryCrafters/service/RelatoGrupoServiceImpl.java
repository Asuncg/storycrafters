package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.repository.RelatoGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelatoGrupoServiceImpl implements  RelatoGrupoService{

    @Autowired
    private RelatoGrupoRepository relatoGrupoRepository;

    @Override
    public Optional<RelatoGrupo> findRelatoGrupoById(int idRelatoGrupo) {
        return relatoGrupoRepository.findById(idRelatoGrupo);
    }

    @Override
    public List<RelatoGrupo> findRelatoGrupoByGrupoIs(Grupo grupo) {
        return relatoGrupoRepository.findByGrupoIs(grupo);
    }

    @Override
    public void guardarRelatoGrupo(RelatoGrupo relatoGrupo) {
        relatoGrupoRepository.save(relatoGrupo);
    }

    @Override
    public Optional<RelatoGrupo> findRelatoGrupoByRelatoAndGrupo(Relato relato, Grupo grupo) {
        return relatoGrupoRepository.findByRelatoAndGrupo(relato, grupo);
    }

    @Override
    public List<RelatoGrupo> buscarRelatosGrupo(Grupo grupo, int estado) {
        return relatoGrupoRepository.findByGrupoAndEstadoOrderByFechaPublicacionDesc(grupo, estado);
    }

    @Override
    public void eliminarRelatoGrupo(int idrelato) {
        relatoGrupoRepository.deleteById(idrelato);
    }
}
