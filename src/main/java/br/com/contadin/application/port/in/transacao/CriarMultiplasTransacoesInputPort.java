package br.com.contadin.application.port.in.transacao;

import br.com.contadin.domain.model.Transacao;

import java.util.List;

public interface CriarMultiplasTransacoesInputPort {
    List<Transacao> execute(List<Transacao> transacoes);
}
