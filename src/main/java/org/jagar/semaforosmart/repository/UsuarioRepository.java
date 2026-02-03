package org.jagar.semaforosmart.repository;

import org.jagar.semaforosmart.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameAndEnabledTrue(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findWithRolesByUsernameAndEnabledTrue(String username);
}
