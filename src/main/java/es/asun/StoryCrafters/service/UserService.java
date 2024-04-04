package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;

import java.util.List;

public interface UserService {


    List<UserRegisterDto> findAllUsers();
    void saveUser(UserRegisterDto userRegisterDto);

    Usuario findUserByEmail(String email);

    void updateUser(UserUpdateDto user);


}
