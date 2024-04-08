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
import java.util.List;
import java.util.Optional;

@Service
public class RelatoServiceImpl implements RelatoService {

@Autowired
private RelatoRepository relatoRepository;
@Autowired
private UserRepository userRepository;
@Autowired
private CategoriaRepository categoriaRepository;

    @Override
    public void guardarRelato(Relato relato, List<Integer> idCategorias) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userRepository.findByEmail(username);

        // Asignar el usuario al relato
        relato.setUsuario(usuario);

        // Obtener las categorías asociadas con los IDs proporcionados
        List<Categoria> categorias = new ArrayList<>();
        for (Integer idCategoria : idCategorias) {
            Optional<Categoria> categoriaOptional = categoriaRepository.findById(idCategoria);
            categoriaOptional.ifPresent(categorias::add);
        }
        relato.setCategorias(categorias);

        // Guardar el relato en la base de datos
        relatoRepository.save(relato);
    }

    @Override
    public List<Relato> findAllRelatosByUsuario(Usuario usuario) {
        return relatoRepository.findByUsuario(usuario);
    }
}
