package br.com.contadin.application.port.out.dashboard;

import br.com.contadin.application.projection.ReceitaMetricsProjection;

import java.time.YearMonth;
import java.util.UUID;

public interface DashMetricsOutputPort {
    ReceitaMetricsProjection buscarMetricasReceitaPeriodo(
            YearMonth periodo,
            UUID usuarioId);
}
