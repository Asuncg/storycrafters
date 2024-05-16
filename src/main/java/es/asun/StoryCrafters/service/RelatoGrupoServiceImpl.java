package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.repository.RelatoGrupoRepository;
import es.asun.StoryCrafters.utilidades.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    public boolean existeRelatoEnviado(int idRelato, int idGrupo) {
        List<Integer> estados = Arrays.asList(Constantes.ESTADO_PENDIENTE, Constantes.ESTADO_APROBADO);
        return relatoGrupoRepository.existeRelatoEnviado(idRelato, idGrupo, estados);
    }

    @Override
    public Optional<RelatoGrupo> findRelatoGrupoByRelatoAndGrupo(Relato relato, Grupo grupo) {
        return relatoGrupoRepository.findByRelatoAndGrupo(relato, grupo);
    }
}
