package br.com.contadin.domain.exception.transacao;


import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class TransacaoInvalidaException extends ApplicationException implements HttpStatusException {

    public TransacaoInvalidaException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
