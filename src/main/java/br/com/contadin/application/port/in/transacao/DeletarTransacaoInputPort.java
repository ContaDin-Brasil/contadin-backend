package br.com.contadin.application.port.in.transacao;

import java.util.UUID;

public interface DeletarTransacaoInputPort {
    void execute(UUID id);
}
