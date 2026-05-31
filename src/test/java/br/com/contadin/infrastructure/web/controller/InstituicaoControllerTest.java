package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.instituicao.InstituicaoRequest;
import br.com.contadin.application.dto.instituicao.InstituicaoResponse;
import br.com.contadin.application.port.in.instituicao.AtualizarInstituicaoInputPort;
import br.com.contadin.application.port.in.instituicao.BuscarInstituicaoInputPort;
import br.com.contadin.application.port.in.instituicao.CriarInstituicaoInputPort;
import br.com.contadin.application.port.in.instituicao.DesativarInstituicaoInputPort;
import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.infrastructure.web.mapper.InstituicaoWebMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstituicaoControllerTest {

    @Mock CriarInstituicaoInputPort criarInstituicaoInputPort;
    @Mock AtualizarInstituicaoInputPort atualizarInstituicaoInputPort;
    @Mock BuscarInstituicaoInputPort buscarInstituicaoInputPort;
    @Mock DesativarInstituicaoInputPort desativarInstituicaoInputPort;
    @Mock InstituicaoWebMapper instituicaoWebMapper;

    @InjectMocks InstituicaoController controller;

    @Test
    void deveCriarInstituicaoERetornar201() {
        InstituicaoRequest request = mock(InstituicaoRequest.class);
        Instituicao instituicao = Instituicao.builder().nome("Nubank").build();
        Instituicao criada = Instituicao.builder().id(UUID.randomUUID()).nome("Nubank").build();
        InstituicaoResponse response = mock(InstituicaoResponse.class);

        when(instituicaoWebMapper.toDomain(request)).thenReturn(instituicao);
        when(criarInstituicaoInputPort.execute(instituicao)).thenReturn(criada);
        when(instituicaoWebMapper.toResponse(criada)).thenReturn(response);

        ResponseEntity<InstituicaoResponse> result = controller.criarInstituicao(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(criarInstituicaoInputPort).execute(instituicao);
    }

    @Test
    void deveAtualizarInstituicaoERetornar200() {
        UUID id = UUID.randomUUID();
        InstituicaoRequest request = mock(InstituicaoRequest.class);
        Instituicao instituicao = Instituicao.builder().nome("Nubank Atualizado").build();
        Instituicao atualizada = Instituicao.builder().id(id).nome("Nubank Atualizado").build();
        InstituicaoResponse response = mock(InstituicaoResponse.class);

        when(instituicaoWebMapper.toDomain(request)).thenReturn(instituicao);
        when(atualizarInstituicaoInputPort.execute(id, instituicao)).thenReturn(atualizada);
        when(instituicaoWebMapper.toResponse(atualizada)).thenReturn(response);

        ResponseEntity<InstituicaoResponse> result = controller.atualizarInstituicao(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(atualizarInstituicaoInputPort).execute(id, instituicao);
    }

    @Test
    void deveDesativarInstituicaoERetornar204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.desativarInstituicao(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(desativarInstituicaoInputPort).execute(id);
    }

    @Test
    void deveBuscarInstituicaoPorIdERetornar200() {
        UUID id = UUID.randomUUID();
        Instituicao instituicao = Instituicao.builder().id(id).build();
        InstituicaoResponse response = mock(InstituicaoResponse.class);

        when(buscarInstituicaoInputPort.executeBuscarPorId(id)).thenReturn(instituicao);
        when(instituicaoWebMapper.toResponse(instituicao)).thenReturn(response);

        ResponseEntity<InstituicaoResponse> result = controller.buscarInstituicaoPorId(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(buscarInstituicaoInputPort).executeBuscarPorId(id);
    }

    @Test
    void deveBuscarInstituicoesPorUsuarioERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        Instituicao instituicao = Instituicao.builder().id(UUID.randomUUID()).build();
        InstituicaoResponse response = mock(InstituicaoResponse.class);

        when(buscarInstituicaoInputPort.execute(fkUsuario)).thenReturn(List.of(instituicao));
        when(instituicaoWebMapper.toResponse(instituicao)).thenReturn(response);

        ResponseEntity<?> result = controller.buscarInstituicoesPorUsuario(fkUsuario);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarInstituicaoInputPort).execute(fkUsuario);
    }

    @Test
    void deveBuscarInstituicoesPorNomeERetornar200() {
        String nome = "Nub";
        UUID fkUsuario = UUID.randomUUID();
        Instituicao instituicao = Instituicao.builder().id(UUID.randomUUID()).nome("Nubank").build();
        InstituicaoResponse response = mock(InstituicaoResponse.class);

        when(buscarInstituicaoInputPort.executeBuscarPorNome(fkUsuario, nome)).thenReturn(List.of(instituicao));
        when(instituicaoWebMapper.toResponse(instituicao)).thenReturn(response);

        ResponseEntity<?> result = controller.buscarInstituicoesPorNome(nome, fkUsuario);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarInstituicaoInputPort).executeBuscarPorNome(fkUsuario, nome);
    }
}
