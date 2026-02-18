package br.com.contadin.application.exception.usuario;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class EmailJaExistenteException
        extends ApplicationException
        implements HttpStatusException {

    public EmailJaExistenteException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
