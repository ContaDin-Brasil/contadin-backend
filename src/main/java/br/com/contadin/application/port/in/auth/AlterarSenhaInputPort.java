package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.usuario.auth.AlterarSenhaRequest;

public interface AlterarSenhaInputPort {
    void execute(AlterarSenhaRequest input);
}
