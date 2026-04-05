package br.com.contadin.application.exception.usuario.auth;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class ConfirmacaoSenhaInvalidaException extends ApplicationException implements HttpStatusException {

    public ConfirmacaoSenhaInvalidaException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
