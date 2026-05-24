package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.transacao.TransacaoConsultaParams;
import br.com.contadin.application.dto.transacao.TransacaoPaginadaResponse;
import br.com.contadin.application.dto.transacao.TransacaoRequest;
import br.com.contadin.application.dto.transacao.TransacaoResponse;
import br.com.contadin.application.port.in.transacao.AtualizarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.BuscarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.CriarMultiplasTransacoesInputPort;
import br.com.contadin.application.port.in.transacao.CriarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.DeletarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.DesativarTransacaoInputPort;
import br.com.contadin.domain.model.Transacao;
import br.com.contadin.infrastructure.web.mapper.TransacaoWebMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    @Mock CriarTransacaoInputPort criarTransacaoInputPort;
    @Mock CriarMultiplasTransacoesInputPort criarMultiplasTransacoesInputPort;
    @Mock BuscarTransacaoInputPort buscarTransacaoInputPort;
    @Mock AtualizarTransacaoInputPort atualizarTransacaoInputPort;
    @Mock DeletarTransacaoInputPort deletarTransacaoInputPort;
    @Mock DesativarTransacaoInputPort desativarTransacaoInputPort;
    @Mock TransacaoWebMapper transacaoWebMapper;

    @InjectMocks TransacaoController controller;

    @Test
    void deveCriarMultiplasTransacoesERetornar201() {
        TransacaoRequest req1 = mock(TransacaoRequest.class);
        TransacaoRequest req2 = mock(TransacaoRequest.class);
        List<TransacaoRequest> requests = List.of(req1, req2);

        Transacao t1 = Transacao.builder().id(UUID.randomUUID()).build();
        Transacao t2 = Transacao.builder().id(UUID.randomUUID()).build();
        TransacaoResponse r1 = mock(TransacaoResponse.class);
        TransacaoResponse r2 = mock(TransacaoResponse.class);

        when(transacaoWebMapper.toDomain(req1)).thenReturn(t1);
        when(transacaoWebMapper.toDomain(req2)).thenReturn(t2);
        when(criarMultiplasTransacoesInputPort.execute(List.of(t1, t2))).thenReturn(List.of(t1, t2));
        when(transacaoWebMapper.toResponse(t1)).thenReturn(r1);
        when(transacaoWebMapper.toResponse(t2)).thenReturn(r2);

        ResponseEntity<List<TransacaoResponse>> result = controller.criarMultiplasTransacoes(requests);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(List.of(r1, r2), result.getBody());
        verify(criarMultiplasTransacoesInputPort).execute(List.of(t1, t2));
    }

    @Test
    void deveCriarTransacaoERetornar201() {
        TransacaoRequest request = mock(TransacaoRequest.class);
        Transacao transacao = Transacao.builder().id(UUID.randomUUID()).build();
        Transacao criada = Transacao.builder().id(UUID.randomUUID()).build();
        TransacaoResponse response = mock(TransacaoResponse.class);

        when(transacaoWebMapper.toDomain(request)).thenReturn(transacao);
        when(criarTransacaoInputPort.execute(transacao)).thenReturn(criada);
        when(transacaoWebMapper.toResponse(criada)).thenReturn(response);

        ResponseEntity<TransacaoResponse> result = controller.criarTransacao(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(criarTransacaoInputPort).execute(transacao);
    }

    @Test
    @SuppressWarnings("unchecked")
    void deveListarTransacoesPaginadasERetornar200() {
        Page<Transacao> page = mock(Page.class);
        Transacao transacao = Transacao.builder().id(UUID.randomUUID()).build();
        TransacaoResponse response = mock(TransacaoResponse.class);

        when(buscarTransacaoInputPort.execute(any(TransacaoConsultaParams.class))).thenReturn(page);
        when(page.getContent()).thenReturn(List.of(transacao));
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(10);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.hasNext()).thenReturn(false);
        when(transacaoWebMapper.toResponse(transacao)).thenReturn(response);

        ResponseEntity<TransacaoPaginadaResponse> result = controller.listarTransacoes(
                null, null, null, null,
                null, null, null,
                null, null, null, null,
                null, null, null
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        TransacaoPaginadaResponse body = result.getBody();
        assertEquals(List.of(response), body.data());
        assertEquals(1, body.page());
        assertEquals(10, body.limit());
        assertEquals(1L, body.total());
        assertEquals(1, body.totalPages());
        assertEquals(false, body.hasMore());
        verify(buscarTransacaoInputPort).execute(any(TransacaoConsultaParams.class));
    }

    @Test
    void deveBuscarTransacaoPorIdERetornar200() {
        UUID id = UUID.randomUUID();
        Transacao transacao = Transacao.builder().id(id).build();
        TransacaoResponse response = mock(TransacaoResponse.class);

        when(buscarTransacaoInputPort.executeBuscarPorId(id)).thenReturn(transacao);
        when(transacaoWebMapper.toResponse(transacao)).thenReturn(response);

        ResponseEntity<TransacaoResponse> result = controller.buscarTransacaoPorId(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(buscarTransacaoInputPort).executeBuscarPorId(id);
    }

    @Test
    void deveAtualizarTransacaoERetornar200() {
        UUID id = UUID.randomUUID();
        TransacaoRequest request = mock(TransacaoRequest.class);
        Transacao transacao = Transacao.builder().id(id).build();
        Transacao atualizada = Transacao.builder().id(id).build();
        TransacaoResponse response = mock(TransacaoResponse.class);

        when(transacaoWebMapper.toDomain(request)).thenReturn(transacao);
        when(atualizarTransacaoInputPort.execute(id, transacao)).thenReturn(atualizada);
        when(transacaoWebMapper.toResponse(atualizada)).thenReturn(response);

        ResponseEntity<TransacaoResponse> result = controller.atualizarTransacao(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(atualizarTransacaoInputPort).execute(id, transacao);
    }

    @Test
    void deveDeletarTransacaoERetornar204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.deletarTransacao(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deletarTransacaoInputPort).execute(id);
    }

    @Test
    void deveDesativarTransacaoERetornar204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.desativarTransacao(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(desativarTransacaoInputPort).execute(id);
    }
}
