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
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AvatarService avatarService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.avatarService = avatarService;
        this.emailService = emailService;
    }

    @Override
    public void saveUser(UserRegisterDto userRegisterDto)  {
        Usuario usuario = new Usuario();
        usuario.setFirstName(userRegisterDto.getFirstName());
        usuario.setLastName(userRegisterDto.getLastName());
        usuario.setEmail(userRegisterDto.getEmail());
        usuario.setAvatar(userRegisterDto.getAvatar());
        usuario.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        Avatar avatar;
        try {
            avatar = avatarService.findAvatarById(1);
            usuario.setAvatar(avatar);

            userRepository.save(usuario);
        } catch (AvatarNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    public boolean processForgotPassword(String email) {
        Optional<Usuario> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            Usuario usuario = userOptional.get();
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            userRepository.save(usuario);

            String resetLink = "https://storycrafters-production.up.railway.app/restablecer-password?token=" + token;
            String message = "Para restablecer tu contraseña, haz clic en el siguiente enlace: " + resetLink;

            emailService.sendEmail(usuario.getEmail(), "Restablecer Contraseña", message);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<Usuario> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent()) {
            Usuario user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);  // Eliminar el token después de usarlo
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Usuario> encontrarUsuarioPorResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }
}