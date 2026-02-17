package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Usuario;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
}
