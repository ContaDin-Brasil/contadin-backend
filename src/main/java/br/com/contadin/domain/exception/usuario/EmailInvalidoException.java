package br.com.contadin.domain.exception.usuario;

import br.com.contadin.domain.exception.DomainException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class EmailInvalidoException
        extends DomainException
        implements HttpStatusException {

    public EmailInvalidoException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}