package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaResponse;

public interface EsqueceuSenhaInputPort {
    EsqueceuSenhaResponse execute(EsqueceuSenhaRequest request);
}
