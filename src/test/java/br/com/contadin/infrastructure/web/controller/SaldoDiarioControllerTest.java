package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.saldo.SaldoDiarioResponse;
import br.com.contadin.application.dto.saldo.SaldoNaDataResponse;
import br.com.contadin.application.dto.saldo.SaldoPorInstituicaoResponse;
import br.com.contadin.application.port.in.saldo.BuscarSaldoDiarioInputPort;
import br.com.contadin.domain.model.SaldoDiario;
import br.com.contadin.infrastructure.web.mapper.SaldoDiarioWebMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaldoDiarioControllerTest {

    @Mock BuscarSaldoDiarioInputPort buscarSaldoDiarioInputPort;
    @Mock SaldoDiarioWebMapper saldoDiarioWebMapper;

    @InjectMocks SaldoDiarioController controller;

    @Test
    void deveBuscarSaldoPorInstituicaoERetornar200() {
        UUID fkInstituicao = UUID.randomUUID();
        LocalDate dataInicio = LocalDate.of(2026, 1, 1);
        LocalDate dataFim = LocalDate.of(2026, 1, 31);

        SaldoDiario saldoDiario = SaldoDiario.builder()
                .fkInstituicao(fkInstituicao)
                .data(dataInicio)
                .saldoFinal(BigDecimal.valueOf(1000.00))
                .build();
        SaldoDiarioResponse response = new SaldoDiarioResponse(dataInicio, BigDecimal.ZERO, BigDecimal.valueOf(1000), BigDecimal.ZERO, BigDecimal.valueOf(1000));

        when(buscarSaldoDiarioInputPort.buscarPorInstituicao(fkInstituicao, dataInicio, dataFim)).thenReturn(List.of(saldoDiario));
        when(saldoDiarioWebMapper.toResponse(saldoDiario)).thenReturn(response);

        ResponseEntity<List<SaldoDiarioResponse>> result = controller.buscarPorInstituicao(fkInstituicao, dataInicio, dataFim);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarSaldoDiarioInputPort).buscarPorInstituicao(fkInstituicao, dataInicio, dataFim);
    }

    @Test
    void deveBuscarSaldoConsolidadoERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate dataInicio = LocalDate.of(2026, 5, 1);
        LocalDate dataFim = LocalDate.of(2026, 5, 31);

        SaldoDiario saldoDiario = SaldoDiario.builder()
                .fkUsuario(fkUsuario)
                .data(dataInicio)
                .saldoFinal(BigDecimal.valueOf(5000.00))
                .build();
        SaldoDiarioResponse response = new SaldoDiarioResponse(dataInicio, BigDecimal.ZERO, BigDecimal.valueOf(5000), BigDecimal.ZERO, BigDecimal.valueOf(5000));

        when(buscarSaldoDiarioInputPort.buscarConsolidado(fkUsuario, dataInicio, dataFim)).thenReturn(List.of(saldoDiario));
        when(saldoDiarioWebMapper.toResponse(saldoDiario)).thenReturn(response);

        ResponseEntity<List<SaldoDiarioResponse>> result = controller.buscarConsolidado(fkUsuario, dataInicio, dataFim);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarSaldoDiarioInputPort).buscarConsolidado(fkUsuario, dataInicio, dataFim);
    }

    @Test
    void deveBuscarSaldoNaDataERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate data = LocalDate.of(2026, 5, 15);
        SaldoNaDataResponse response = new SaldoNaDataResponse(data, BigDecimal.valueOf(3500.00), BigDecimal.valueOf(1000), BigDecimal.valueOf(500));

        when(buscarSaldoDiarioInputPort.buscarSaldoNaData(fkUsuario, data)).thenReturn(response);

        ResponseEntity<SaldoNaDataResponse> result = controller.buscarSaldoNaData(fkUsuario, data);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(buscarSaldoDiarioInputPort).buscarSaldoNaData(fkUsuario, data);
    }

    @Test
    void deveBuscarSaldoPorInstituicoesNaDataERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        UUID fkInstituicao = UUID.randomUUID();
        LocalDate data = LocalDate.of(2026, 5, 15);
        SaldoPorInstituicaoResponse saldoResp = new SaldoPorInstituicaoResponse(
                fkInstituicao, data, BigDecimal.valueOf(2000), BigDecimal.valueOf(500), BigDecimal.valueOf(200)
        );

        when(buscarSaldoDiarioInputPort.buscarSaldoPorInstituicoesNaData(fkUsuario, fkInstituicao, data))
                .thenReturn(List.of(saldoResp));

        ResponseEntity<List<SaldoPorInstituicaoResponse>> result = controller.buscarSaldoPorInstituicoesNaData(fkUsuario, data, fkInstituicao);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(saldoResp), result.getBody());
        verify(buscarSaldoDiarioInputPort).buscarSaldoPorInstituicoesNaData(fkUsuario, fkInstituicao, data);
    }
}
