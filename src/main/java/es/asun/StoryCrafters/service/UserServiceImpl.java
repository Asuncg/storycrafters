package es.asun.StoryCrafters.service;


import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserRegisterDto userRegisterDto) {
        Usuario usuario = new Usuario();
        usuario.setFirstName(userRegisterDto.getFirstName());
        usuario.setLastName(userRegisterDto.getLastName());
        usuario.setEmail(userRegisterDto.getEmail());
        // encrypt the password using spring security
        usuario.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        userRepository.save(usuario);
    }

    @Override
    public Usuario findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUser(UserUpdateDto user) {
        updateValidation();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Usuario usuario = userRepository.findByEmail(username);
        if (usuario != null) {
            if (user.getFirstName() != null) {
                usuario.setFirstName(user.getFirstName());
            }
            if (user.getLastName() != null) {
                usuario.setLastName(user.getLastName());
            }
            if (user.getFirmaAutor() != null) {
                usuario.setFirmaAutor(user.getFirmaAutor());
            }
            userRepository.save(usuario);
        }
    }

    @Override
    public Optional<Usuario> findUserById(int id) {
        return userRepository.findById(id);
    }


    private void updateValidation() {
    }

    @Override
    public List<UserRegisterDto> findAllUsers() {
        List<Usuario> usuarios = userRepository.findAll();
        return usuarios.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserRegisterDto mapToUserDto(Usuario usuario){
        UserRegisterDto userRegisterDto = new UserRegisterDto();

        userRegisterDto.setFirstName(usuario.getFirstName());
        userRegisterDto.setLastName(usuario.getLastName());
        userRegisterDto.setEmail(usuario.getEmail());
        userRegisterDto.setFirmaAutor(usuario.getFirmaAutor());
        return userRegisterDto;
    }

    private Usuario mapToUser(UserRegisterDto userRegisterDto) {
        Usuario usuario = new Usuario();
        usuario.setId(userRegisterDto.getId());
        usuario.setFirstName(userRegisterDto.getFirstName());
        usuario.setLastName(userRegisterDto.getLastName());
        usuario.setFirmaAutor(userRegisterDto.getFirmaAutor());
        return usuario;
    }

}