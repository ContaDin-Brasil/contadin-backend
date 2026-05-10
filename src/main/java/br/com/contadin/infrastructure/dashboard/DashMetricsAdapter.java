package br.com.contadin.infrastructure.dashboard;

import br.com.contadin.application.port.out.dashboard.DashMetricsOutputPort;
import br.com.contadin.application.projection.ReceitaMetricsProjection;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.infrastructure.persistence.repository.transacao.TransacaoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
    public ReceitaMetricsProjection buscarMetricasReceitaPeriodo(
            YearMonth periodo,
            UUID usuarioId
    ) {

        LocalDateTime inicio = periodo
                .atDay(1)
                .atStartOfDay();

        LocalDateTime fim = periodo
                .atEndOfMonth()
                .atTime(LocalTime.MAX);

        return transacaoRepository.buscarMetricasReceitaPeriodo(
                inicio,
                fim,
                TipoTransacao.RECEITA,
                usuarioId
        );
    }
    }

