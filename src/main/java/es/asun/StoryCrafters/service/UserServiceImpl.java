package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.model.User;
import es.asun.StoryCrafters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findall() {
        return userRepository.findAll();
    }

    @Override
    public User findAllByNombre(String nombre) {
        return null;
    }


}
