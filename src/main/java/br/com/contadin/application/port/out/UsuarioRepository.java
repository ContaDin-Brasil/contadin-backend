package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findById(UUID id);
}
