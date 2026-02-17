package br.com.contadin.application.port.in.usuario;

import br.com.contadin.domain.model.Usuario;

public interface CriarUsuarioInputPort {
    Usuario execute(Usuario usuario);
}
