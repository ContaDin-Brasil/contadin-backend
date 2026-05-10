package br.com.contadin.application.port.in.dashboard;

import br.com.contadin.application.dto.dashboard.DashReceitaResponse;

import java.time.YearMonth;
import java.util.UUID;

public interface DashQueryInputPort {
    DashReceitaResponse buscarReceitasPorPeriodo(
            YearMonth periodo,
            UUID usuarioId
    );
}
