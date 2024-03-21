package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    List<User> findall();

    User findAllByNombre(String nombre);

}
