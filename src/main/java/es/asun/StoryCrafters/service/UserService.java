package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();
    void saveUser(UserDto userDto);

    Usuario findUserByEmail(String email);

    void updateUser(UserDto user);


}
