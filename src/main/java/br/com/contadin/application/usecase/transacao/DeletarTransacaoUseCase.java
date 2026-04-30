package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.DeletarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarTransacaoUseCase implements DeletarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new TransacaoInvalidaException("ID da transação é obrigatório para exclusão.");
        }

        if (!transacaoRepository.findById(id).isPresent()) {
            throw new TransacaoNaoEncontradaException("Transação não encontrada.");
        }

        transacaoRepository.deleteById(id);
    }
}