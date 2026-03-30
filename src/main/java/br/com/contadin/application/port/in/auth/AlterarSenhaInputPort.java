package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.usuario.auth.AlterarSenhaInputDTO;

public interface AlterarSenhaInputPort {
    void execute(AlterarSenhaInputDTO input);
}
