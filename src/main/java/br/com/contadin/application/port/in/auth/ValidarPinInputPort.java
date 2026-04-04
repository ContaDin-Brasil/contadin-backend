package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinResponse;

public interface ValidarPinInputPort {
    ValidarPinResponse execute(ValidarPinRequest request);
}
