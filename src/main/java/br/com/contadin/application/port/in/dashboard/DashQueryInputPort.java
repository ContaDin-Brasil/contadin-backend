package br.com.contadin.application.port.in.dashboard;

import br.com.contadin.application.dto.dashboard.DashReceitaGastoResponse;

import java.time.YearMonth;
import java.util.UUID;

public interface DashQueryInputPort {
    DashReceitaGastoResponse buscarReceitasPorPeriodo(
            YearMonth periodo,
            UUID usuarioId
    );

    DashReceitaGastoResponse buscarGastosPorPeriodo(
            YearMonth periodo,
            UUID usuarioId
    );
}
