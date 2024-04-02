package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);
}
