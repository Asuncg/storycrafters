package es.asun.StoryCrafters.service;


import es.asun.StoryCrafters.entity.Role;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.model.UserDto;
import es.asun.StoryCrafters.repository.RoleRepository;
import es.asun.StoryCrafters.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        Usuario usuario = new Usuario();
        usuario.setFirstName(userDto.getFirstName() + " " + userDto.getLastName());
        usuario.setEmail(userDto.getEmail());
        // encrypt the password using spring security
        usuario.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if(role == null){
            role = checkRoleExist();
        }
        usuario.setRoles(Arrays.asList(role));
        userRepository.save(usuario);
    }

    @Override
    public Usuario findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUser(UserDto user) {
        updateValidation();
        Usuario usuarioMapped = mapToUser(user);
        userRepository.save(usuarioMapped);
    }

    private void updateValidation() {
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<Usuario> usuarios = userRepository.findAll();
        return usuarios.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(Usuario usuario){
        UserDto userDto = new UserDto();

        userDto.setFirstName(usuario.getFirstName());
        userDto.setLastName(usuario.getLastName());
        userDto.setEmail(usuario.getEmail());
        userDto.setFirmaAutor(usuario.getFirmaAutor());
        return userDto;
    }

    private Usuario mapToUser(UserDto userDto) {
        Usuario usuario = new Usuario();
        usuario.setId(userDto.getId());
        usuario.setFirstName(userDto.getFirstName());
        usuario.setLastName(userDto.getLastName());
        usuario.setFirmaAutor(userDto.getFirmaAutor());
        return usuario;
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }
}