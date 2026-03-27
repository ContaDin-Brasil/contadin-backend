package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.BuscarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarTransacaoUseCase implements BuscarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;

    @Override
    public List<Transacao> execute() {
        return transacaoRepository.findAll();
    }

    @Override
    public Transacao executeBuscarPorId(Integer transacaoId) {
        if (transacaoId == null) {
            throw new TransacaoInvalidaException("ID da transação é obrigatório para busca.");
        }

        return transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new TransacaoNaoEncontradaException("Transação não encontrada."));
    }
}
