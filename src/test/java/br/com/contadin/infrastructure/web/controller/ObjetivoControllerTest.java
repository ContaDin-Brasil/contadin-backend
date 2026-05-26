package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.objetivo.ObjetivoRequest;
import br.com.contadin.application.dto.objetivo.ObjetivoResponse;
import br.com.contadin.application.dto.objetivo.kpi.ImpactoPrevistoKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.MaiorAlertaKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.NoRitmoKpiResponse;
import br.com.contadin.application.port.in.objetivo.AtualizarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.BuscarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.CriarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.DeletarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.ObjetivoKpiInputPort;
import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.infrastructure.web.mapper.ObjetivoWebMapper;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjetivoControllerTest {

    @Mock CriarObjetivoInputPort criarObjetivoInputPort;
    @Mock AtualizarObjetivoInputPort atualizarObjetivoInputPort;
    @Mock BuscarObjetivoInputPort buscarObjetivoInputPort;
    @Mock DeletarObjetivoInputPort deletarObjetivoInputPort;
    @Mock ObjetivoKpiInputPort objetivoKpiInputPort;
    @Mock ObjetivoWebMapper objetivoWebMapper;

    @InjectMocks ObjetivoController controller;

    @Test
    void deveCriarObjetivoERetornar201() {
        ObjetivoRequest request = mock(ObjetivoRequest.class);
        Objetivo objetivo = Objetivo.builder().nome("Economizar").build();
        Objetivo criado = Objetivo.builder().id(UUID.randomUUID()).nome("Economizar").build();
        ObjetivoResponse response = mock(ObjetivoResponse.class);

        when(objetivoWebMapper.toDomain(request)).thenReturn(objetivo);
        when(criarObjetivoInputPort.execute(objetivo)).thenReturn(criado);
        when(objetivoWebMapper.toResponse(criado)).thenReturn(response);

        ResponseEntity<ObjetivoResponse> result = controller.criarObjetivo(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(criarObjetivoInputPort).execute(objetivo);
    }

    @Test
    void deveAtualizarObjetivoERetornar200() {
        UUID id = UUID.randomUUID();
        ObjetivoRequest request = mock(ObjetivoRequest.class);
        Objetivo objetivo = Objetivo.builder().nome("Meta Atualizada").build();
        Objetivo atualizado = Objetivo.builder().id(id).nome("Meta Atualizada").build();
        ObjetivoResponse response = mock(ObjetivoResponse.class);

        when(objetivoWebMapper.toDomain(request)).thenReturn(objetivo);
        when(atualizarObjetivoInputPort.execute(id, objetivo)).thenReturn(atualizado);
        when(objetivoWebMapper.toResponse(atualizado)).thenReturn(response);

        ResponseEntity<ObjetivoResponse> result = controller.atualizarObjetivo(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(atualizarObjetivoInputPort).execute(id, objetivo);
    }

    @Test
    void deveBuscarObjetivoPorIdERetornar200() {
        UUID id = UUID.randomUUID();
        Objetivo objetivo = Objetivo.builder().id(id).build();
        ObjetivoResponse response = mock(ObjetivoResponse.class);

        when(buscarObjetivoInputPort.executeBuscarPorId(id)).thenReturn(objetivo);
        when(objetivoWebMapper.toResponse(objetivo)).thenReturn(response);

        ResponseEntity<ObjetivoResponse> result = controller.buscarObjetivoPorId(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(buscarObjetivoInputPort).executeBuscarPorId(id);
    }

    @Test
    void deveBuscarObjetivosPorUsuarioERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        Boolean concluido = false;
        Objetivo objetivo = Objetivo.builder().id(UUID.randomUUID()).build();
        ObjetivoResponse response = mock(ObjetivoResponse.class);

        when(buscarObjetivoInputPort.execute(fkUsuario, concluido)).thenReturn(List.of(objetivo));
        when(objetivoWebMapper.toResponse(objetivo)).thenReturn(response);

        ResponseEntity<List<ObjetivoResponse>> result = controller.buscarObjetivoPorUsuario(fkUsuario, concluido);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarObjetivoInputPort).execute(fkUsuario, concluido);
    }

    @Test
    void deveBuscarObjetivosPorNomeERetornar200() {
        String nome = "Econom";
        UUID fkUsuario = UUID.randomUUID();
        Boolean concluido = null;
        Objetivo objetivo = Objetivo.builder().id(UUID.randomUUID()).nome("Economizar").build();
        ObjetivoResponse response = mock(ObjetivoResponse.class);

        when(buscarObjetivoInputPort.executeBuscarPorNome(nome, fkUsuario, concluido)).thenReturn(List.of(objetivo));
        when(objetivoWebMapper.toResponse(objetivo)).thenReturn(response);

        ResponseEntity<List<ObjetivoResponse>> result = controller.buscarObjetivoPorNome(nome, fkUsuario, concluido);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarObjetivoInputPort).executeBuscarPorNome(nome, fkUsuario, concluido);
    }

    @Test
    void deveDeletarObjetivoERetornar204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.deletarObjetivo(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deletarObjetivoInputPort).execute(id);
    }

    @Test
    void deveBuscarImpactoPrevistoKpiERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate dataInicio = LocalDate.of(2026, 1, 1);
        LocalDate dataFim = LocalDate.of(2026, 12, 31);
        TipoObjetivo tipo = TipoObjetivo.LIMITE_GASTO;
        ImpactoPrevistoKpiResponse kpiResponse = new ImpactoPrevistoKpiResponse(BigDecimal.valueOf(5000.00));

        when(objetivoKpiInputPort.impactoPrevisto(fkUsuario, dataInicio, dataFim, tipo)).thenReturn(kpiResponse);

        ResponseEntity<ImpactoPrevistoKpiResponse> result = controller.impactoPrevisto(fkUsuario, dataInicio, dataFim, tipo);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(kpiResponse, result.getBody());
        verify(objetivoKpiInputPort).impactoPrevisto(fkUsuario, dataInicio, dataFim, tipo);
    }

    @Test
    void deveBuscarNoRitmoKpiERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate dataInicio = LocalDate.of(2026, 1, 1);
        LocalDate dataFim = LocalDate.of(2026, 12, 31);
        TipoObjetivo tipo = TipoObjetivo.AUMENTO_RECEITA;
        NoRitmoKpiResponse kpiResponse = new NoRitmoKpiResponse(3, 5, List.of());

        when(objetivoKpiInputPort.noRitmo(fkUsuario, dataInicio, dataFim, tipo)).thenReturn(kpiResponse);

        ResponseEntity<NoRitmoKpiResponse> result = controller.noRitmo(fkUsuario, dataInicio, dataFim, tipo);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(kpiResponse, result.getBody());
        verify(objetivoKpiInputPort).noRitmo(fkUsuario, dataInicio, dataFim, tipo);
    }

    @Test
    void deveBuscarMaiorAlertaKpiERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate dataInicio = LocalDate.of(2026, 1, 1);
        LocalDate dataFim = LocalDate.of(2026, 12, 31);
        TipoObjetivo tipo = TipoObjetivo.LIMITE_GASTO;
        UUID objetivoId = UUID.randomUUID();
        MaiorAlertaKpiResponse kpiResponse = new MaiorAlertaKpiResponse("Alimentação", objetivoId, StatusObjetivo.CUIDADO, TipoObjetivo.LIMITE_GASTO);

        when(objetivoKpiInputPort.maiorAlerta(fkUsuario, dataInicio, dataFim, tipo)).thenReturn(kpiResponse);

        ResponseEntity<MaiorAlertaKpiResponse> result = controller.maiorAlerta(fkUsuario, dataInicio, dataFim, tipo);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(kpiResponse, result.getBody());
        verify(objetivoKpiInputPort).maiorAlerta(fkUsuario, dataInicio, dataFim, tipo);
    }
}
