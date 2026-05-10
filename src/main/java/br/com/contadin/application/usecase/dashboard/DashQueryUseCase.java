package br.com.contadin.application.usecase.dashboard;

import br.com.contadin.application.dto.dashboard.DashReceitaResponse;
import br.com.contadin.application.port.in.dashboard.DashQueryInputPort;
import br.com.contadin.application.port.out.dashboard.DashMetricsOutputPort;
import br.com.contadin.application.projection.ReceitaMetricsProjection;
import br.com.contadin.domain.enums.TipoTransacao;

import java.time.YearMonth;
import java.util.UUID;

public class DashQueryUseCase
        implements DashQueryInputPort {

    private final DashMetricsOutputPort dashMetricsOutputPort;

    public DashQueryUseCase(
            DashMetricsOutputPort dashMetricsOutputPort
    ) {
        this.dashMetricsOutputPort = dashMetricsOutputPort;
    }

    @Override
    public DashReceitaResponse buscarReceitasPorPeriodo(
            YearMonth periodo,
            UUID usuarioId
    ) {

        ReceitaMetricsProjection projection =
                dashMetricsOutputPort
                        .buscarMetricasReceitaPeriodo(periodo, usuarioId);

        return new DashReceitaResponse(
                periodo.getMonthValue(),
                TipoTransacao.RECEITA,
                projection.valorTotal()
        );
    }
}