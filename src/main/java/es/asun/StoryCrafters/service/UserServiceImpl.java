package es.asun.StoryCrafters.service;


import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.exceptions.AvatarNotFoundException;
import es.asun.StoryCrafters.exceptions.UsuarioException;
import es.asun.StoryCrafters.model.UserRegisterDto;
import es.asun.StoryCrafters.model.UserUpdateDto;
import es.asun.StoryCrafters.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AvatarService avatarService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AvatarService avatarService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.avatarService = avatarService;
    }

    @Override
    public void saveUser(UserRegisterDto userRegisterDto) {
        Usuario usuario = new Usuario();
        usuario.setFirstName(userRegisterDto.getFirstName());
        usuario.setLastName(userRegisterDto.getLastName());
        usuario.setEmail(userRegisterDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        Optional<Avatar> avatarOptional = avatarService.findAvatarById(1);

        if (avatarOptional.isPresent()) {
            Avatar avatar = avatarOptional.get();
            userRegisterDto.setAvatar(avatar);
        } else {
            throw new AvatarNotFoundException("Default avatar not found");
        }
        userRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUser(UserUpdateDto user) {
        updateValidation();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(username);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
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
    public Usuario findUserById(int id) throws UsuarioException {
        Optional<Usuario> usuarioOptional = userRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            throw new UsuarioException("Usuario no encontrado");
        }
        return usuarioOptional.get();
    }


    private void updateValidation() {
    }
}