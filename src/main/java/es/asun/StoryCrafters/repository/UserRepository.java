package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByNombre(String nombre);

    boolean findByEmail(String email);
}
