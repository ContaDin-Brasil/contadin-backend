package br.com.contadin.application.port.out.dashboard;

import br.com.contadin.application.projection.ReceitaGastoMetricsProjection;

import java.time.YearMonth;
import java.util.UUID;

public interface DashMetricsOutputPort {
    ReceitaGastoMetricsProjection buscarMetricasReceitaPeriodo(
            YearMonth periodo,
            UUID usuarioId);

    ReceitaGastoMetricsProjection buscarMetricasGastoPeriodo(
            YearMonth periodo,
            UUID usuarioId);
}
