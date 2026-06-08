package org.jagar.semaforosmart.service;

import org.jagar.semaforosmart.entity.Usuario;
import org.jagar.semaforosmart.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public DatabaseUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoAndActivoTrue(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o deshabilitado"));

        String rol = usuario.getRol() == null ? "" : usuario.getRol().toUpperCase(Locale.ROOT);
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));

        return new User(
                usuario.getCorreo(),
                usuario.getPasswordHash(),
                usuario.isActivo(),
                true,
                true,
                true,
                authorities
        );
    }
}
