package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Transacao;

public interface TransacaoRepository {
    Transacao save(Transacao transacao);
}
