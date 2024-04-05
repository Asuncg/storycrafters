package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Relato;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.repository.RelatoRepository;
import es.asun.StoryCrafters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RelatoServiceImpl implements RelatoService {

@Autowired
private RelatoRepository relatoRepository;
@Autowired
private UserRepository userRepository;
    @Override
    public void guardarRelato(Relato relato) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userRepository.findByEmail(username);

        // Asignar el usuario al relato
        relato.setUsuario(usuario);

        // Guardar el relato en la base de datos
        relatoRepository.save(relato);
    }

}
