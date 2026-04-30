package br.com.contadin.application.port.in.tokenRecuperarSenha;

import br.com.contadin.domain.model.TokenRecuperarSenha;

public interface CriarTokenRecuperarSenhaInputPort {
    TokenRecuperarSenha execute(TokenRecuperarSenha tokenRecuperarSenha);
}
