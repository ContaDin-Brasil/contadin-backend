package br.com.contadin.infrastructure.dashboard;

import br.com.contadin.application.port.out.dashboard.DashMetricsOutputPort;
import br.com.contadin.application.projection.ReceitaGastoMetricsProjection;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.infrastructure.persistence.repository.transacao.TransacaoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.UUID;

@Component
public class DashMetricsAdapter implements DashMetricsOutputPort {

        private final TransacaoJpaRepository transacaoRepository;

        public DashMetricsAdapter(
                TransacaoJpaRepository transacaoRepository
        ) {
            this.transacaoRepository = transacaoRepository;
        }

    @Override
    public ReceitaGastoMetricsProjection buscarTipoTransacaoPorPeriodo(
            YearMonth periodo,
            UUID usuarioId,
            TipoTransacao tipo
    ) {

        LocalDateTime inicio = periodo
                .atDay(1)
                .atStartOfDay();

        LocalDateTime fim = periodo
                .atEndOfMonth()
                .atTime(LocalTime.MAX);

        return transacaoRepository.buscarMetricasReceitaGastoPeriodo(
                inicio,
                fim,
                tipo,
                usuarioId
        );
    }
}

