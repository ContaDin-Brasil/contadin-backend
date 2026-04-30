package br.com.contadin.domain.exception.transacao;
import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.infrastructure.web.exception.HttpStatusException;
import org.springframework.http.HttpStatus;

public class TransacaoNaoEncontradaException extends ApplicationException implements HttpStatusException {

    public TransacaoNaoEncontradaException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
