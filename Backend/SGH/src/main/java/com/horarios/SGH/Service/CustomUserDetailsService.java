package com.horarios.SGH.Service;

import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.Iusers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio personalizado para cargar detalles de usuario para autenticación.
 * Maneja la carga de usuarios desde la base de datos y proporciona un usuario master como fallback.
 * Ahora incluye permisos granulares basados en roles.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Value("${app.master.username}")
    private String masterUsername;

    @Value("${app.master.password}")
    private String masterPassword;

    private final Iusers userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    public CustomUserDetailsService(Iusers userRepository, PasswordEncoder passwordEncoder, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionService = permissionService;
    }

    /**
     * Carga un usuario por su nombre de usuario (email).
     * Primero intenta cargar desde la base de datos, luego usa el usuario master como fallback.
     * Ahora incluye permisos granulares basados en roles.
     *
     * @param username El nombre de usuario (email) a buscar
     * @return UserDetails del usuario encontrado
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Intentar cargar desde base de datos
        users user = userRepository.findByUserName(username).orElse(null);
        if (user != null) {
            // Verificar que el email coincida exactamente
            if (!user.getPerson().getEmail().equals(username)) {
                throw new UsernameNotFoundException("Usuario no encontrado: " + username);
            }

            // Obtener permisos del usuario
            Set<String> userPermissions = permissionService.getUserPermissions(user.getUserId());

            // Convertir permisos a GrantedAuthority
            Set<SimpleGrantedAuthority> authorities = userPermissions.stream()
                    .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission))
                    .collect(Collectors.toSet());

            // Agregar también el rol como autoridad
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()));

            return User.withUsername(user.getPerson().getEmail())
                    .password(user.getPasswordHash())
                    .authorities(authorities)
                    .build();
        }

        // Fallback para usuario master solo si no existe en BD
        if (masterUsername.equals(username)) {
            return User.withUsername(masterUsername)
                    .password(passwordEncoder.encode(masterPassword))
                    .roles("COORDINADOR")
                    .build();
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}