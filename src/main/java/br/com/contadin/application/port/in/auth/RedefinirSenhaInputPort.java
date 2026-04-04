package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaResponse;

public interface RedefinirSenhaInputPort {
    RedefinirSenhaResponse execute(RedefinirSenhaRequest request);
}
