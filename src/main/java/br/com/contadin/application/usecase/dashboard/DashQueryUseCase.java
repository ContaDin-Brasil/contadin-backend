package br.com.contadin.application.usecase.dashboard;

import br.com.contadin.application.dto.dashboard.DashReceitaGastoResponse;
import br.com.contadin.application.port.in.dashboard.DashQueryInputPort;
import br.com.contadin.application.port.out.dashboard.DashMetricsOutputPort;
import br.com.contadin.application.projection.ReceitaGastoMetricsProjection;
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
    public DashReceitaGastoResponse buscarReceitasPorPeriodo(
            YearMonth periodo,
            UUID usuarioId
    ) {

        ReceitaGastoMetricsProjection projection =
                dashMetricsOutputPort
                        .buscarMetricasReceitaPeriodo(periodo, usuarioId);

        return new DashReceitaGastoResponse(
                periodo.getMonthValue(),
                TipoTransacao.RECEITA,
                projection.valorTotal()
        );
    }

    @Override
    public DashReceitaGastoResponse buscarGastosPorPeriodo(
            YearMonth periodo,
            UUID usuarioId
    ) {

        ReceitaGastoMetricsProjection projection =
                dashMetricsOutputPort
                        .buscarMetricasGastoPeriodo(periodo, usuarioId);

        return new DashReceitaGastoResponse(
                periodo.getMonthValue(),
                TipoTransacao.GASTO,
                projection.valorTotal()
        );
    }
}