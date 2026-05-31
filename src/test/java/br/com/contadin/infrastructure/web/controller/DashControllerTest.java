package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.dashboard.DashReceitaGastoResponse;
import br.com.contadin.application.port.in.dashboard.DashQueryInputPort;
import br.com.contadin.domain.enums.TipoTransacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashControllerTest {

    @Mock DashQueryInputPort dashQueryInputPort;

    @InjectMocks DashController controller;

    @Test
    void deveBuscarIndicadoresComPeriodoInformadoERetornarResponse() {
        YearMonth periodo = YearMonth.of(2026, 3);
        UUID usuarioId = UUID.randomUUID();
        TipoTransacao tipo = TipoTransacao.GASTO;
        DashReceitaGastoResponse response = new DashReceitaGastoResponse(3, TipoTransacao.GASTO, 1500.00);

        when(dashQueryInputPort.buscarTipoTransacaoPorPeriodo(periodo, usuarioId, tipo)).thenReturn(response);

        DashReceitaGastoResponse result = controller.buscarReceita(periodo, tipo, usuarioId);

        assertEquals(response, result);
        verify(dashQueryInputPort).buscarTipoTransacaoPorPeriodo(periodo, usuarioId, tipo);
    }

    @Test
    void deveBuscarIndicadoresComPeriodoNullUsandoYearMonthAtual() {
        UUID usuarioId = UUID.randomUUID();
        TipoTransacao tipo = TipoTransacao.RECEITA;
        DashReceitaGastoResponse response = new DashReceitaGastoResponse(YearMonth.now().getMonthValue(), TipoTransacao.RECEITA, 3000.00);

        when(dashQueryInputPort.buscarTipoTransacaoPorPeriodo(any(YearMonth.class), eq(usuarioId), eq(tipo)))
                .thenReturn(response);

        DashReceitaGastoResponse result = controller.buscarReceita(null, tipo, usuarioId);

        assertEquals(response, result);
        verify(dashQueryInputPort).buscarTipoTransacaoPorPeriodo(any(YearMonth.class), eq(usuarioId), eq(tipo));
    }
}
