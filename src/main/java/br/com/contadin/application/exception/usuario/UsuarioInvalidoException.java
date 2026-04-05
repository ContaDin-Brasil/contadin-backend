package br.com.contadin.application.exception.usuario;


import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class UsuarioInvalidoException extends ApplicationException implements HttpStatusException {

    public UsuarioInvalidoException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
