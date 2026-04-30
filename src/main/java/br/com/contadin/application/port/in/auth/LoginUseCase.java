package br.com.contadin.application.port.in.auth;

import br.com.contadin.application.dto.usuario.auth.LoginRequest;
import br.com.contadin.application.dto.usuario.auth.LoginResponse;

public interface LoginUseCase {

    LoginResponse execute(LoginRequest input);
}
