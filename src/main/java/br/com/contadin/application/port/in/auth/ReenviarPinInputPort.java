package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinResponse;

public interface ReenviarPinInputPort {
    ReenviarPinResponse execute(ReenviarPinRequest request);
}
