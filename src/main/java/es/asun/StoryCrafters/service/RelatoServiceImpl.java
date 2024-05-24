package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.RelatoException;
import es.asun.StoryCrafters.model.RelatoPreviewDto;
import es.asun.StoryCrafters.repository.RelatoRepository;
import es.asun.StoryCrafters.utils.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<Relato> findAllRelatosByUsuarioAndNotArchivado(Usuario usuario) {
        return relatoRepository.findByUsuarioAndArchivadoFalse(usuario);
    }

    @Override
    public Relato findRelatoByIdAndNotArchivado(int id) throws RelatoException {
        Optional<Relato> relatoOptional = relatoRepository.findRelatoByIdAndNotArchivado(id);

        if (relatoOptional.isEmpty()) {
            throw new RelatoException("Relato no encontrado");
        }
        return relatoOptional.get();
    }

    @Override
    public List<RelatoPreviewDto> findAllRelatoByUsuarioOrderByFecha(Usuario usuario) {
        List<Relato> relatos = relatoRepository.findByUsuarioAndArchivadoFalseOrderByFechaActualizacionDesc(usuario);

        List<RelatoPreviewDto> relatosDto = new ArrayList<>();

        for (Relato relato : relatos) {
            RelatoPreviewDto relatoDto = Mappings.mapToRelatoVistaDto(relato);
            relatosDto.add(relatoDto);
        }
        return relatosDto;
    }
}