package br.com.contadin.domain.exception.categoria;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class CategoriaInvalidaException extends ApplicationException implements HttpStatusException {

    public CategoriaInvalidaException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}