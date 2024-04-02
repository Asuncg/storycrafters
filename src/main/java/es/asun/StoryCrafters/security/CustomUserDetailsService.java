package es.asun.StoryCrafters.security;


import es.asun.StoryCrafters.entity.Role;
import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByEmail(email);

        if (usuario != null) {
            return new org.springframework.security.core.userdetails.User(usuario.getEmail(),
                    usuario.getPassword(),
                    mapRolesToAuthorities(usuario.getRoles()));
        }else{
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }
    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Collection <Role> roles) {
        Collection < ? extends GrantedAuthority> mapRoles = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return mapRoles;
    }

}