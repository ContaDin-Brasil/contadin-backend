package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.DeletarMultiplasTransacoesInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletarMultiplasTransacoesUseCase implements DeletarMultiplasTransacoesInputPort {

    private final TransacaoRepository transacaoRepository;
    private final SaldoDiarioCalculadorService calculadorService;

    @Override
    @Transactional
    public void execute(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new TransacaoInvalidaException("A lista de IDs não pode ser vazia.");
        }

        List<Transacao> encontradas = transacaoRepository.findAllByIds(ids);

        if (encontradas.size() != ids.size()) {
            throw new TransacaoNaoEncontradaException("Uma ou mais transações não foram encontradas.");
        }

        transacaoRepository.deleteAllByIds(ids);

        encontradas.stream()
                .collect(Collectors.groupingBy(
                        Transacao::getFkInstituicao,
                        Collectors.minBy(Comparator.comparing(t -> t.getDataTransacao().toLocalDate()))
                ))
                .forEach((instId, optTransacao) ->
                        optTransacao.ifPresent(t -> {
                            try {
                                calculadorService.recalcular(instId, t.getDataTransacao().toLocalDate());
                            } catch (Exception e) {
                                log.warn("Falha ao recalcular saldo diário da instituição {} após deleção em lote: {}", instId, e.getMessage());
                            }
                        }));
    }
}
