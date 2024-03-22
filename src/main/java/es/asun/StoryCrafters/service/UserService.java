package es.asun.StoryCrafters.service;

import es.asun.StoryCrafters.entity.User;
import es.asun.StoryCrafters.model.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}
