package br.com.contadin.application.exception.objetivo;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class ObjetivoInvalidoException extends ApplicationException implements HttpStatusException {

    public ObjetivoInvalidoException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
