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
    public DashReceitaGastoResponse buscarTipoTransacaoPorPeriodo(
            YearMonth periodo,
            UUID usuarioId,
            TipoTransacao tipo
    ) {

        ReceitaGastoMetricsProjection projection =
                dashMetricsOutputPort
                        .buscarTipoTransacaoPorPeriodo(periodo, usuarioId, tipo);

        return new DashReceitaGastoResponse(
                periodo.getMonthValue(),
                tipo,
                projection.valorTotal()
        );
    }
}