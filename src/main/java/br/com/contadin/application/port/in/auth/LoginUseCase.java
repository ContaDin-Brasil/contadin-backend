package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.usuario.auth.LoginInputDTO;
import br.com.contadin.application.dto.usuario.auth.LoginOutputDTO;

public interface LoginUseCase {

    LoginOutputDTO execute(LoginInputDTO input);
}
