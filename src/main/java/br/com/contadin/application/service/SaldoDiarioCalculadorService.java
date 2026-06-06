package br.com.contadin.application.service;

import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.SaldoDiarioRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.domain.model.SaldoDiario;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaldoDiarioCalculadorService {

    private final SaldoDiarioRepository saldoDiarioRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final TransacaoRepository transacaoRepository;

    @Transactional
    public void recalcular(UUID fkInstituicao, LocalDate dataMinima) {
        Instituicao instituicao = instituicaoRepository.findById(fkInstituicao)
                .orElseThrow(() -> new IllegalStateException("Instituição não encontrada: " + fkInstituicao));

        UUID fkUsuario = instituicao.getFkUsuario();

        BigDecimal saldoSemente = saldoDiarioRepository.buscarUltimoAntesDeData(fkInstituicao, dataMinima)
                .map(SaldoDiario::getSaldoFinal)
                .orElseGet(() -> instituicao.getSaldoInicial() != null ? instituicao.getSaldoInicial() : BigDecimal.ZERO);

        saldoDiarioRepository.deletarAPartirDeData(fkInstituicao, dataMinima);

        List<Transacao> transacoes = transacaoRepository.findAllAtivasByInstituicao(fkInstituicao);

        LocalDate dataFim = LocalDate.now().plusMonths(6);
        List<SaldoDiario> snapshots = new ArrayList<>();
        BigDecimal saldoAtual = saldoSemente;

        for (LocalDate data = dataMinima; !data.isAfter(dataFim); data = data.plusDays(1)) {
            final LocalDate dataAtual = data;
            BigDecimal totalReceitas = BigDecimal.ZERO;
            BigDecimal totalGastos = BigDecimal.ZERO;

            for (Transacao t : transacoes) {
                if (!ocorreNaData(t, dataAtual)) continue;
                BigDecimal valor = Boolean.TRUE.equals(t.getParcelado()) && t.getQtdParcelas() != null && t.getQtdParcelas() > 0
                        ? BigDecimal.valueOf(t.getValor()).divide(BigDecimal.valueOf(t.getQtdParcelas()), 2, RoundingMode.HALF_UP)
                        : BigDecimal.valueOf(t.getValor());
                if (t.getTipo() == TipoTransacao.RECEITA) {
                    totalReceitas = totalReceitas.add(valor);
                } else {
                    totalGastos = totalGastos.add(valor);
                }
            }

            BigDecimal saldoFinal = saldoAtual.add(totalReceitas).subtract(totalGastos);

            snapshots.add(SaldoDiario.builder()
                    .fkInstituicao(fkInstituicao)
                    .fkUsuario(fkUsuario)
                    .data(dataAtual)
                    .saldoInicial(saldoAtual)
                    .totalReceitas(totalReceitas)
                    .totalGastos(totalGastos)
                    .saldoFinal(saldoFinal)
                    .calculadoEm(LocalDateTime.now())
                    .build());

            saldoAtual = saldoFinal;
        }

        saldoDiarioRepository.salvarTodos(snapshots);
    }

    private boolean ocorreNaData(Transacao transacao, LocalDate data) {
        LocalDate dataTransacao = transacao.getDataTransacao().toLocalDate();
        if (data.isBefore(dataTransacao)) return false;

        if (transacao.getFimRecorrencia() != null) {
            LocalDate fimRecorrencia = transacao.getFimRecorrencia().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            if (data.isAfter(fimRecorrencia)) return false;
        }

        if (Boolean.TRUE.equals(transacao.getParcelado())) {
            int n = transacao.getQtdParcelas() != null ? transacao.getQtdParcelas() : 1;
            for (int i = 0; i < n; i++) {
                if (data.isEqual(dataTransacao.plusMonths(i))) return true;
            }
            return false;
        }

        if (transacao.getRecorrencia() == null) {
            return data.isEqual(dataTransacao);
        }

        return switch (transacao.getRecorrencia()) {
            case DIARIO -> true;
            case SEMANAL -> ChronoUnit.DAYS.between(dataTransacao, data) % 7 == 0;
            case MENSAL -> data.getDayOfMonth() == dataTransacao.getDayOfMonth()
                    || (data.getDayOfMonth() == data.lengthOfMonth()
                        && dataTransacao.getDayOfMonth() > data.lengthOfMonth());
            case ANUAL -> data.getDayOfMonth() == dataTransacao.getDayOfMonth()
                    && data.getMonthValue() == dataTransacao.getMonthValue();
        };
    }
}
