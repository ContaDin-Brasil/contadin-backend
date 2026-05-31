package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.DeletarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletarTransacaoUseCase implements DeletarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;
    private final SaldoDiarioCalculadorService calculadorService;

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new TransacaoInvalidaException("ID da transação é obrigatório para exclusão.");
        }

        Transacao existente = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException("Transação não encontrada."));

        transacaoRepository.deleteById(id);

        try {
            calculadorService.recalcular(existente.getFkInstituicao(), existente.getDataTransacao().toLocalDate());
        } catch (Exception e) {
            log.warn("Falha ao recalcular saldo diário após deletar transação {}: {}", id, e.getMessage());
        }
    }
}