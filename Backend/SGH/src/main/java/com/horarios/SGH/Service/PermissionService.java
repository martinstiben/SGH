package com.horarios.SGH.Service;

import com.horarios.SGH.Model.Permissions;
import com.horarios.SGH.Model.PermissionsRoles;
import com.horarios.SGH.Model.Roles;
import com.horarios.SGH.Model.users;
import com.horarios.SGH.Repository.IPermissionsRepository;
import com.horarios.SGH.Repository.IPermissionsRolesRepository;
import com.horarios.SGH.Repository.IRolesRepository;
import com.horarios.SGH.Repository.Iusers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar permisos basados en roles.
 * Proporciona métodos para verificar permisos de usuarios y obtener permisos por rol.
 */
@Service
public class PermissionService {

    private final IPermissionsRepository permissionsRepository;
    private final IPermissionsRolesRepository permissionsRolesRepository;
    private final IRolesRepository rolesRepository;
    private final Iusers userRepository;

    public PermissionService(IPermissionsRepository permissionsRepository,
                           IPermissionsRolesRepository permissionsRolesRepository,
                           IRolesRepository rolesRepository,
                           Iusers userRepository) {
        this.permissionsRepository = permissionsRepository;
        this.permissionsRolesRepository = permissionsRolesRepository;
        this.rolesRepository = rolesRepository;
        this.userRepository = userRepository;
    }

    /**
     * Verifica si un usuario tiene un permiso específico.
     *
     * @param userId ID del usuario
     * @param permissionName Nombre del permiso a verificar
     * @return true si el usuario tiene el permiso, false en caso contrario
     */
    public boolean hasPermission(int userId, String permissionName) {
        users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        Roles role = user.getRole();
        if (role == null) {
            return false;
        }

        List<PermissionsRoles> permissionsRoles = permissionsRolesRepository.findByRole_RoleId(role.getRoleId());

        return permissionsRoles.stream()
                .anyMatch(pr -> pr.getPermission().getPermissionName().equals(permissionName));
    }

    /**
     * Verifica si un usuario tiene un permiso específico por email.
     *
     * @param email Email del usuario
     * @param permissionName Nombre del permiso a verificar
     * @return true si el usuario tiene el permiso, false en caso contrario
     */
    public boolean hasPermissionByEmail(String email, String permissionName) {
        users user = userRepository.findByUserName(email).orElse(null);
        if (user == null) {
            return false;
        }

        return hasPermission(user.getUserId(), permissionName);
    }

    /**
     * Obtiene todos los permisos asignados a un usuario.
     *
     * @param userId ID del usuario
     * @return Set con los nombres de permisos del usuario
     */
    public Set<String> getUserPermissions(int userId) {
        users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Set.of();
        }

        Roles role = user.getRole();
        if (role == null) {
            return Set.of();
        }

        List<PermissionsRoles> permissionsRoles = permissionsRolesRepository.findByRole_RoleId(role.getRoleId());

        return permissionsRoles.stream()
                .map(pr -> pr.getPermission().getPermissionName())
                .collect(Collectors.toSet());
    }

    /**
     * Obtiene todos los permisos asignados a un usuario por email.
     *
     * @param email Email del usuario
     * @return Set con los nombres de permisos del usuario
     */
    public Set<String> getUserPermissions(String email) {
        users user = userRepository.findByUserName(email).orElse(null);
        if (user == null) {
            return Set.of();
        }

        return getUserPermissions(user.getUserId());
    }

    /**
     * Obtiene todos los permisos asignados a un rol.
     *
     * @param roleId ID del rol
     * @return Set con los nombres de permisos del rol
     */
    public Set<String> getRolePermissions(int roleId) {
        List<PermissionsRoles> permissionsRoles = permissionsRolesRepository.findByRole_RoleId(roleId);

        return permissionsRoles.stream()
                .map(pr -> pr.getPermission().getPermissionName())
                .collect(Collectors.toSet());
    }

    /**
     * Obtiene todos los permisos asignados a un rol por nombre.
     *
     * @param roleName Nombre del rol
     * @return Set con los nombres de permisos del rol
     */
    public Set<String> getRolePermissions(String roleName) {
        Roles role = rolesRepository.findByRoleName(roleName).orElse(null);
        if (role == null) {
            return Set.of();
        }

        return getRolePermissions(role.getRoleId());
    }

    /**
     * Verifica si un rol tiene un permiso específico.
     *
     * @param roleId ID del rol
     * @param permissionName Nombre del permiso
     * @return true si el rol tiene el permiso, false en caso contrario
     */
    public boolean roleHasPermission(int roleId, String permissionName) {
        List<PermissionsRoles> permissionsRoles = permissionsRolesRepository.findByRole_RoleId(roleId);

        return permissionsRoles.stream()
                .anyMatch(pr -> pr.getPermission().getPermissionName().equals(permissionName));
    }

    /**
     * Verifica si un rol tiene un permiso específico por nombre de rol.
     *
     * @param roleName Nombre del rol
     * @param permissionName Nombre del permiso
     * @return true si el rol tiene el permiso, false en caso contrario
     */
    public boolean roleHasPermission(String roleName, String permissionName) {
        Roles role = rolesRepository.findByRoleName(roleName).orElse(null);
        if (role == null) {
            return false;
        }

        return roleHasPermission(role.getRoleId(), permissionName);
    }

    /**
     * Verifica si un usuario puede acceder a recursos de un área específica.
     * Los directores de área solo pueden gestionar recursos de su área asignada.
     *
     * @param userId ID del usuario
     * @param resourceArea Área del recurso al que se quiere acceder
     * @return true si el usuario puede acceder al recurso, false en caso contrario
     */
    public boolean canAccessArea(int userId, String resourceArea) {
        users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // Coordinadores tienen acceso a todas las áreas
        if ("COORDINADOR".equals(user.getRole().getRoleName())) {
            return true;
        }

        // Directores de área solo pueden acceder a su área asignada
        if ("DIRECTOR_DE_AREA".equals(user.getRole().getRoleName())) {
            return user.getArea() != null && user.getArea().equals(resourceArea);
        }

        // Otros roles no tienen restricciones de área
        return true;
    }

    /**
     * Verifica si un usuario puede acceder a recursos de un área específica por email.
     *
     * @param email Email del usuario
     * @param resourceArea Área del recurso al que se quiere acceder
     * @return true si el usuario puede acceder al recurso, false en caso contrario
     */
    public boolean canAccessArea(String email, String resourceArea) {
        users user = userRepository.findByUserName(email).orElse(null);
        if (user == null) {
            return false;
        }

        return canAccessArea(user.getUserId(), resourceArea);
    }
}