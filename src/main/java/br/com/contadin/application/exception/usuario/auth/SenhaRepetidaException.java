package br.com.contadin.application.exception.usuario.auth;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class SenhaRepetidaException extends ApplicationException implements HttpStatusException {
    public SenhaRepetidaException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
