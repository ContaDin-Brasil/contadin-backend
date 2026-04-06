package br.com.contadin.application.port.in.transacao;

import br.com.contadin.application.dto.transacao.TransacaoConsultaParams;
import br.com.contadin.domain.model.Transacao;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface BuscarTransacaoInputPort {
    List<Transacao> execute();

    Page<Transacao> execute(TransacaoConsultaParams params);

    Transacao executeBuscarPorId(UUID transacaoId);

}
