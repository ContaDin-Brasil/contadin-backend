package br.com.contadin.application.exception.objetivo;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class ObjetivoNaoEncontradoException extends ApplicationException implements HttpStatusException {

    public ObjetivoNaoEncontradoException() {
        super("Objetivo não encontrado");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
