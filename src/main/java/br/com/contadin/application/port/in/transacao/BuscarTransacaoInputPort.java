package br.com.contadin.application.port.in.transacao;

import br.com.contadin.domain.model.Transacao;

import java.util.List;

public interface BuscarTransacaoInputPort {
    List<Transacao> execute();

    Transacao executeBuscarPorId(Integer transacaoId);

}
