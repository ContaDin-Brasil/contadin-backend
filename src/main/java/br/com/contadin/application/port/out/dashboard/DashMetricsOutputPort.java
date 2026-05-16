package br.com.contadin.application.port.out.dashboard;

import br.com.contadin.application.projection.ReceitaGastoMetricsProjection;
import br.com.contadin.domain.enums.TipoTransacao;

import java.time.YearMonth;
import java.util.UUID;

public interface DashMetricsOutputPort {
    ReceitaGastoMetricsProjection buscarTipoTransacaoPorPeriodo(
            YearMonth periodo,
            UUID usuarioId,
            TipoTransacao tipo);
}
