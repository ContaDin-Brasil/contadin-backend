package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.CriarMultiplasTransacoesInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriarMultiplasTransacoesUseCase implements CriarMultiplasTransacoesInputPort {

    private final TransacaoRepository transacaoRepository;
    private final SaldoDiarioCalculadorService calculadorService;

    @Override
    public List<Transacao> execute(List<Transacao> transacoes) {
        if (transacoes == null || transacoes.isEmpty()) {
            throw new TransacaoInvalidaException("A lista de transações não pode ser vazia.");
        }

        List<Transacao> paraSalvar = new ArrayList<>();

        for (int i = 0; i < transacoes.size(); i++) {
            Transacao transacao = transacoes.get(i);
            int posicao = i + 1;
            validarTransacao(transacao, posicao);

            Integer qtdParcelas = ajustarQtdParcelas(transacao.getParcelado(), transacao.getQtdParcelas());

            paraSalvar.add(Transacao.builder()
                    .id(transacao.getId())
                    .valor(transacao.getValor())
                    .tipo(transacao.getTipo())
                    .descricao(transacao.getDescricao())
                    .dataTransacao(transacao.getDataTransacao())
                    .parcelado(transacao.getParcelado())
                    .qtdParcelas(qtdParcelas)
                    .recorrencia(transacao.getRecorrencia())
                    .fimRecorrencia(transacao.getFimRecorrencia())
                    .ativo(transacao.getAtivo() != null ? transacao.getAtivo() : true)
                    .fkInstituicao(transacao.getFkInstituicao())
                    .fkCategoria(transacao.getFkCategoria())
                    .build());
        }

        List<Transacao> salvas = transacaoRepository.saveAll(paraSalvar);

        salvas.stream()
                .collect(Collectors.groupingBy(
                        Transacao::getFkInstituicao,
                        Collectors.minBy(Comparator.comparing(t -> t.getDataTransacao().toLocalDate()))
                ))
                .forEach((instId, optTransacao) ->
                        optTransacao.ifPresent(t -> {
                            try {
                                calculadorService.recalcular(instId, t.getDataTransacao().toLocalDate());
                            } catch (Exception e) {
                                log.warn("Falha ao recalcular saldo diário para instituição {}: {}", instId, e.getMessage());
                            }
                        }));

        return salvas;
    }

    private void validarTransacao(Transacao transacao, int posicao) {
        if (transacao.getValor() == null || transacao.getValor() <= 0) {
            throw new TransacaoInvalidaException(
                    "Transação " + posicao + ": o valor deve ser maior que zero.");
        }
        if (transacao.getTipo() == null) {
            throw new TransacaoInvalidaException(
                    "Transação " + posicao + ": o tipo é obrigatório.");
        }
        if (transacao.getDataTransacao() == null) {
            throw new TransacaoInvalidaException(
                    "Transação " + posicao + ": a data é obrigatória.");
        }
        if (transacao.getParcelado() == null) {
            throw new TransacaoInvalidaException(
                    "Transação " + posicao + ": o campo parcelado é obrigatório.");
        }
        validarParcelamento(transacao.getParcelado(), transacao.getQtdParcelas(), posicao);
    }

    private void validarParcelamento(Boolean parcelado, Integer qtdParcelas, int posicao) {
        if (Boolean.TRUE.equals(parcelado)) {
            if (qtdParcelas == null) {
                throw new TransacaoInvalidaException(
                        "Transação " + posicao + ": a quantidade de parcelas é obrigatória para transações parceladas.");
            }
            if (qtdParcelas < 2 || qtdParcelas > 720) {
                throw new TransacaoInvalidaException(
                        "Transação " + posicao + ": a quantidade de parcelas deve estar entre 2 e 720.");
            }
        }
    }

    private Integer ajustarQtdParcelas(Boolean parcelado, Integer qtdParcelas) {
        return Boolean.FALSE.equals(parcelado) ? null : qtdParcelas;
    }
}
