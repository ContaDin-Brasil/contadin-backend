package br.com.contadin.application.port.in.transacao;

import br.com.contadin.domain.model.Transacao;

public interface AtualizarTransacaoInputPort {
    Transacao execute(Integer id, Transacao transacao);

}
