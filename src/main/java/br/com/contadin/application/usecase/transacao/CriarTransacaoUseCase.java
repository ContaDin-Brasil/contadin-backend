package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.CriarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarTransacaoUseCase implements CriarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;

    @Override
    public Transacao execute(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }

}
