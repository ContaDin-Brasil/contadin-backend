package br.com.contadin.application.port.in.transacao;

import br.com.contadin.domain.model.Transacao;

import java.util.UUID;

public interface AtualizarTransacaoInputPort {
    Transacao execute(UUID id, Transacao transacao);

}
