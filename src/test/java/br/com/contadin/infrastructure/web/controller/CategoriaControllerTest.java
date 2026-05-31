package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.application.dto.categoria.CategoriaResponse;
import br.com.contadin.application.dto.categoria.GastoPorCategoriaResponse;
import br.com.contadin.application.port.in.categoria.AlternarStatusCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.AtualizarCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.BuscarCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.BuscarGastoPorCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.CriarCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.DeletarCategoriaInputPort;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.web.mapper.CategoriaWebMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock CriarCategoriaInputPort criarCategoriaInputPort;
    @Mock AtualizarCategoriaInputPort atualizarCategoriaInputPort;
    @Mock BuscarCategoriaInputPort buscarCategoriaInputPort;
    @Mock BuscarGastoPorCategoriaInputPort buscarGastoPorCategoriaInputPort;
    @Mock DeletarCategoriaInputPort deletarCategoriaInputPort;
    @Mock AlternarStatusCategoriaInputPort alternarStatusCategoriaInputPort;
    @Mock CategoriaWebMapper categoriaWebMapper;

    @InjectMocks CategoriaController controller;

    @Test
    void deveCriarCategoriaERetornar201() {
        CategoriaRequest request = new CategoriaRequest("Alimentação", "🍔", "#FF0000", TipoCategoria.GASTO, UUID.randomUUID());
        Categoria categoria = Categoria.builder().nome("Alimentação").build();
        Categoria categoriaCriada = Categoria.builder().id(UUID.randomUUID()).nome("Alimentação").build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(categoriaWebMapper.toDomain(request)).thenReturn(categoria);
        when(criarCategoriaInputPort.execute(categoria)).thenReturn(categoriaCriada);
        when(categoriaWebMapper.toResponse(categoriaCriada)).thenReturn(response);

        ResponseEntity<CategoriaResponse> result = controller.criar(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(criarCategoriaInputPort).execute(categoria);
    }

    @Test
    void deveAtualizarCategoriaERetornar200() {
        UUID id = UUID.randomUUID();
        CategoriaRequest request = new CategoriaRequest("Transporte", "🚗", "#00FF00", TipoCategoria.GASTO, UUID.randomUUID());
        Categoria categoria = Categoria.builder().nome("Transporte").build();
        Categoria atualizada = Categoria.builder().id(id).nome("Transporte").build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(categoriaWebMapper.toDomain(request)).thenReturn(categoria);
        when(atualizarCategoriaInputPort.execute(id, categoria)).thenReturn(atualizada);
        when(categoriaWebMapper.toResponse(atualizada)).thenReturn(response);

        ResponseEntity<CategoriaResponse> result = controller.atualizar(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(atualizarCategoriaInputPort).execute(id, categoria);
    }

    @Test
    void deveBuscarCategoriaPorIdERetornar200() {
        UUID id = UUID.randomUUID();
        Categoria categoria = Categoria.builder().id(id).build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(buscarCategoriaInputPort.executeBuscarPorId(id)).thenReturn(categoria);
        when(categoriaWebMapper.toResponse(categoria)).thenReturn(response);

        ResponseEntity<CategoriaResponse> result = controller.buscarPorId(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(buscarCategoriaInputPort).executeBuscarPorId(id);
    }

    @Test
    void deveListarTodasCategoriasERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        Categoria categoria = Categoria.builder().id(UUID.randomUUID()).build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(buscarCategoriaInputPort.executeBuscarTodas(fkUsuario)).thenReturn(List.of(categoria));
        when(categoriaWebMapper.toResponse(categoria)).thenReturn(response);

        ResponseEntity<List<CategoriaResponse>> result = controller.listarTodasPorUsuario(fkUsuario);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarCategoriaInputPort).executeBuscarTodas(fkUsuario);
    }

    @Test
    void deveListarCategoriasPorUsuarioETipoERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        TipoCategoria tipo = TipoCategoria.GASTO;
        Categoria categoria = Categoria.builder().id(UUID.randomUUID()).tipo(tipo).build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(buscarCategoriaInputPort.execute(fkUsuario, tipo)).thenReturn(List.of(categoria));
        when(categoriaWebMapper.toResponse(categoria)).thenReturn(response);

        ResponseEntity<List<CategoriaResponse>> result = controller.listarPorUsuario(fkUsuario, tipo);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarCategoriaInputPort).execute(fkUsuario, tipo);
    }

    @Test
    void deveListarCategoriasInativasERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        Categoria categoria = Categoria.builder().id(UUID.randomUUID()).ativo(false).build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(buscarCategoriaInputPort.executeBuscarInativas(fkUsuario)).thenReturn(List.of(categoria));
        when(categoriaWebMapper.toResponse(categoria)).thenReturn(response);

        ResponseEntity<List<CategoriaResponse>> result = controller.listarCategoriasInativas(fkUsuario);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarCategoriaInputPort).executeBuscarInativas(fkUsuario);
    }

    @Test
    void deveBuscarCategoriasPorNomeERetornar200() {
        String nome = "Aliment";
        UUID fkUsuario = UUID.randomUUID();
        TipoCategoria tipo = TipoCategoria.GASTO;
        Categoria categoria = Categoria.builder().id(UUID.randomUUID()).nome("Alimentação").build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(buscarCategoriaInputPort.executeBuscarPorNome(nome, fkUsuario, tipo)).thenReturn(List.of(categoria));
        when(categoriaWebMapper.toResponse(categoria)).thenReturn(response);

        ResponseEntity<List<CategoriaResponse>> result = controller.buscarPorNome(nome, fkUsuario, tipo);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(response), result.getBody());
        verify(buscarCategoriaInputPort).executeBuscarPorNome(nome, fkUsuario, tipo);
    }

    @Test
    void deveDeletarCategoriaERetornar204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.deletar(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deletarCategoriaInputPort).execute(id);
    }

    @Test
    void deveAlternarStatusCategoriaERetornar204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.alternarStatus(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(alternarStatusCategoriaInputPort).execute(id);
    }

    @Test
    void deveBuscarGastoPorCategoriaERetornar200() {
        UUID fkUsuario = UUID.randomUUID();
        UUID fkInstituicao = UUID.randomUUID();
        int mes = 5;
        int ano = 2026;
        GastoPorCategoriaResponse gastoResponse = new GastoPorCategoriaResponse(
                UUID.randomUUID(), "Alimentação", "🍔", "#FF0000",
                BigDecimal.valueOf(850.00), BigDecimal.valueOf(56.67)
        );

        when(buscarGastoPorCategoriaInputPort.execute(fkUsuario, mes, ano, fkInstituicao))
                .thenReturn(List.of(gastoResponse));

        ResponseEntity<List<GastoPorCategoriaResponse>> result = controller.buscarGastoPorCategoria(fkUsuario, mes, ano, fkInstituicao);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(gastoResponse), result.getBody());
        verify(buscarGastoPorCategoriaInputPort).execute(fkUsuario, mes, ano, fkInstituicao);
    }
}
