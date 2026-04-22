package br.com.contadin.application.port.in.usuario;

import br.com.contadin.domain.model.Usuario;

import java.util.List;

public interface ListarUsuariosInputPort {
    List<Usuario> execute();
}
