package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.TokenRecuperarSenha;

public interface TokenRecuperarSenhaRepository {
    TokenRecuperarSenha save(TokenRecuperarSenha tokenRecuperarSenha);
}
