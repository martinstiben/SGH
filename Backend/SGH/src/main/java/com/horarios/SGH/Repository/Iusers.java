package com.horarios.SGH.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.horarios.SGH.Model.Role;
import com.horarios.SGH.Model.users;

public interface Iusers extends JpaRepository<users, Integer> {
    Optional<users> findByEmail(String email);
    boolean existsByEmail(String email);
    long count();
    List<users> findByRole(Role role);

    // Para compatibilidad con autenticación
    default Optional<users> findByUserName(String userName) {
        return findByEmail(userName);
    }

    default boolean existsByUserName(String userName) {
        return existsByEmail(userName);
    }
}