package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;

import java.util.List;
import java.util.Optional;

public interface UserService {


    List<UserRegisterDto> findAllUsers();
    void saveUser(UserRegisterDto userRegisterDto);

    Usuario findUserByEmail(String email);

    void updateUser(UserUpdateDto user);

    Optional<Usuario> findUserById(int id);

}
