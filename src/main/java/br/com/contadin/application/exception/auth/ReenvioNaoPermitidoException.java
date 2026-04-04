package br.com.contadin.application.exception.auth;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class ReenvioNaoPermitidoException extends ApplicationException implements HttpStatusException {

    public ReenvioNaoPermitidoException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
