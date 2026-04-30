package br.com.contadin.application.port.in.usuario;

import br.com.contadin.domain.model.Usuario;

import java.util.UUID;

public interface AtualizarUsuarioInputPort {
    Usuario execute(UUID id, Usuario usuario);
}
