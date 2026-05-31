package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.AtualizarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtualizarTransacaoUseCase implements AtualizarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;
    private final SaldoDiarioCalculadorService calculadorService;

    @Override
    public Transacao execute(UUID id, Transacao transacao) {
        if (id == null) {
            throw new TransacaoInvalidaException("ID da transação é obrigatório para atualização.");
        }

        Transacao existente = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException("Transação não encontrada."));

        Boolean parcelado = transacao.getParcelado() != null ? transacao.getParcelado() : existente.getParcelado();
        Integer qtdParcelas = transacao.getQtdParcelas() != null ? transacao.getQtdParcelas() : existente.getQtdParcelas();
        qtdParcelas = ajustarQtdParcelas(parcelado, qtdParcelas);

        Transacao toSave = Transacao.builder()
                .id(existente.getId())
                .valor(transacao.getValor() != null ? transacao.getValor() : existente.getValor())
                .tipo(transacao.getTipo() != null ? transacao.getTipo() : existente.getTipo())
                .descricao(transacao.getDescricao() != null ? transacao.getDescricao() : existente.getDescricao())
                .dataTransacao(transacao.getDataTransacao() != null ? transacao.getDataTransacao() : existente.getDataTransacao())
                .parcelado(parcelado)
                .qtdParcelas(qtdParcelas)
                .recorrencia(transacao.getRecorrencia() != null ? transacao.getRecorrencia() : existente.getRecorrencia())
                .fimRecorrencia(transacao.getFimRecorrencia() != null ? transacao.getFimRecorrencia() : existente.getFimRecorrencia())
                .ativo(transacao.getAtivo() != null ? transacao.getAtivo() : existente.getAtivo())
                .fkInstituicao(transacao.getFkInstituicao() != null ? transacao.getFkInstituicao() : existente.getFkInstituicao())
                .fkCategoria(transacao.getFkCategoria() != null ? transacao.getFkCategoria() : existente.getFkCategoria())
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

            validarTransacao(toSave);

        Transacao salva = transacaoRepository.save(toSave);

        LocalDate dataAnterior = existente.getDataTransacao().toLocalDate();
        LocalDate dataAtual = salva.getDataTransacao().toLocalDate();
        LocalDate dataMinima = dataAnterior.isBefore(dataAtual) ? dataAnterior : dataAtual;

        try {
            calculadorService.recalcular(salva.getFkInstituicao(), dataMinima);
        } catch (Exception e) {
            log.warn("Falha ao recalcular saldo diário após atualizar transação {}: {}", salva.getId(), e.getMessage());
        }

        if (!salva.getFkInstituicao().equals(existente.getFkInstituicao())) {
            try {
                calculadorService.recalcular(existente.getFkInstituicao(), dataAnterior);
            } catch (Exception e) {
                log.warn("Falha ao recalcular saldo diário da instituição anterior {}: {}", existente.getFkInstituicao(), e.getMessage());
            }
        }

        return salva;
    }

    private void validarTransacao(Transacao transacao) {
        if (transacao.getValor() == null || transacao.getValor() <= 0) {
            throw new TransacaoInvalidaException("O valor da transação deve ser maior que zero.");
        }
        if (transacao.getTipo() == null) {
            throw new TransacaoInvalidaException("O tipo da transação é obrigatório.");
        }
        if (transacao.getDataTransacao() == null) {
            throw new TransacaoInvalidaException("A data da transação é obrigatória.");
        }
        if (transacao.getParcelado() == null) {
            throw new TransacaoInvalidaException("O campo parcelado é obrigatório.");
        }
        validarParcelamento(transacao.getParcelado(), transacao.getQtdParcelas());
    }

    private void validarParcelamento(Boolean parcelado, Integer qtdParcelas) {
        if (Boolean.TRUE.equals(parcelado)) {
            if (qtdParcelas == null) {
                throw new TransacaoInvalidaException("A quantidade de parcelas é obrigatória para transações parceladas.");
            }
            if (qtdParcelas < 2 || qtdParcelas > 720) {
                throw new TransacaoInvalidaException("A quantidade de parcelas deve estar entre 2 e 720.");
            }
        }
    }

    private Integer ajustarQtdParcelas(Boolean parcelado, Integer qtdParcelas) {
        return Boolean.FALSE.equals(parcelado) ? null : qtdParcelas;
    }
}
