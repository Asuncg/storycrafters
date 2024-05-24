package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveUser(UserRegisterDto userRegisterDto);

    Optional<Usuario> findUserByEmail(String email);

    void updateUser(UserUpdateDto user);

    Usuario findUserById(int id) throws UsuarioException;

}
