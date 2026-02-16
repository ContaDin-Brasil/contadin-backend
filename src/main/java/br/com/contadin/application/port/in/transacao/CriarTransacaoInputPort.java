package br.com.contadin.application.port.in.transacao;

import br.com.contadin.application.dto.transacao.TransacaoRequest;
import br.com.contadin.domain.model.Transacao;

public interface CriarTransacaoInputPort {
    Transacao execute(Transacao transacao);
}
