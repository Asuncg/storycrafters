package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Grupo;
import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.RelatoGrupo;
import es.asun.StoryCrafters.exceptions.RelatoGrupoException;
import es.asun.StoryCrafters.model.RelatoGrupoDto;
import es.asun.StoryCrafters.model.RelatoGrupoGestionDto;
import es.asun.StoryCrafters.repository.RelatoGrupoRepository;
import es.asun.StoryCrafters.utils.Constantes;
import es.asun.StoryCrafters.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.asun.StoryCrafters.utils.Constantes.*;

@Service
public class RelatoGrupoServiceImpl implements RelatoGrupoService {

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

    @Override
    public Map<Integer, Long> contarRelatosAprobadosPorUsuarioEnGrupo(Grupo grupo) {
        List<RelatoGrupo> listaRelatosGrupo = findRelatoGrupoByGrupoIs(grupo);
        return listaRelatosGrupo.stream()
                .filter(relato -> relato.getEstado() == Constantes.ESTADO_APROBADO)
                .collect(Collectors.groupingBy(relato -> relato.getRelato().getUsuario().getId(), Collectors.counting()));
    }


    @Override
    public void gestionarRelato(RelatoGrupoGestionDto relatoGrupoGestionDto) throws RelatoGrupoException {
        int idRelatoGrupo = relatoGrupoGestionDto.getId();

        Optional<RelatoGrupo> relatoGrupoOptional = this.findRelatoGrupoById(idRelatoGrupo);

        if (relatoGrupoOptional.isPresent()) {
            RelatoGrupo relatoGrupo = relatoGrupoOptional.get();

            if (relatoGrupoGestionDto.isAprobado()) {
                relatoGrupo.setEstado(ESTADO_APROBADO);
            } else {
                relatoGrupo.setEstado(ESTADO_RECHAZADO);
            }

            relatoGrupo.setCalificacion(relatoGrupoGestionDto.getCalificacion());
            relatoGrupo.setFeedback(relatoGrupoGestionDto.getFeedback());
            relatoGrupo.setFechaModificacion(new Date());
            this.guardarRelatoGrupo(relatoGrupo);
        } else {
            throw new RelatoGrupoException("Relato de grupo no encontrado");
        }
    }

    @Override
    public RelatoGrupoDto encontrarRelatoGrupoPorId(int id) throws RelatoGrupoException{
        Optional<RelatoGrupo> relatoGrupoOptional = this.findRelatoGrupoById(id);
        if (relatoGrupoOptional.isEmpty()) {
            throw new RelatoGrupoException("Relato de grupo no encontrado");
        }
        RelatoGrupo relatoGrupo = relatoGrupoOptional.get();
        return Mappings.mapToRelatoGrupoDto(relatoGrupo);
    }


}
