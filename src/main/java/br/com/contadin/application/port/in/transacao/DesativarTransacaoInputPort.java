package br.com.contadin.application.port.in.transacao;

import java.util.UUID;

public interface DesativarTransacaoInputPort {
    void execute(UUID id);
}
