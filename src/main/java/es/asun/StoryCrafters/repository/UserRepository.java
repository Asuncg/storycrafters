package es.asun.StoryCrafters.repository;

import es.asun.StoryCrafters.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
