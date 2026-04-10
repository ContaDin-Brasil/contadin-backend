package br.com.contadin.domain.exception.categoria;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class CategoriaNaoEncontradaException extends ApplicationException implements HttpStatusException {

    public CategoriaNaoEncontradaException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}