package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.User;
import es.asun.StoryCrafters.model.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    void updateUser(UserDto user);


}
