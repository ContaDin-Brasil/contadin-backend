package br.com.contadin.application.port.in.dashboard;

import br.com.contadin.application.dto.dashboard.DashReceitaGastoResponse;
import br.com.contadin.domain.enums.TipoTransacao;

import java.time.YearMonth;
import java.util.UUID;

public interface DashQueryInputPort {
    DashReceitaGastoResponse buscarTipoTransacaoPorPeriodo(
            YearMonth periodo,
            UUID usuarioId,
            TipoTransacao tipo
    );
}
