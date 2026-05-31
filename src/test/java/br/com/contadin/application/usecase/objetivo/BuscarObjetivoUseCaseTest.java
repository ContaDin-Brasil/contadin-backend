package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.exception.objetivo.ObjetivoNaoEncontradoException;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.model.Objetivo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarObjetivoUseCaseTest {

    @Mock
    private ObjetivoRepository objetivoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private BuscarObjetivoUseCase useCase;

    private Objetivo objetivoBase() {
        return Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("Limitar delivery")
                .valor(BigDecimal.valueOf(450))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(UUID.randomUUID())
                .fkCategoria(UUID.randomUUID())
                .build();
    }

    @Test
    void deveBuscarObjetivoPorIdQuandoUuidValido() {
        // Arrange
        Objetivo objetivo = objetivoBase();
        when(objetivoRepository.findById(objetivo.getId())).thenReturn(Optional.of(objetivo));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(200));

        // Act
        Objetivo resultado = useCase.executeBuscarPorId(objetivo.getId());

        // Assert
        assertNotNull(resultado);
        assertEquals(objetivo.getNome(), resultado.getNome());
        assertEquals(BigDecimal.valueOf(200), resultado.getRealizado());
    }

    @Test
    void deveLancarErroQuandoBuscarPorIdComUuidNulo() {
        assertThrows(ObjetivoInvalidoException.class, () -> useCase.executeBuscarPorId(null));
    }

    @Test
    void deveLancarErroQuandoObjetivoNaoEncontradoPorId() {
        UUID id = UUID.randomUUID();
        when(objetivoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ObjetivoNaoEncontradoException.class, () -> useCase.executeBuscarPorId(id));
    }

    @Test
    void deveBuscarObjetivosPorUsuario() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        Objetivo obj = objetivoBase();
        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(obj));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        // Act
        List<Objetivo> resultado = useCase.execute(usuarioId);

        // Assert
        assertEquals(1, resultado.size());
        assertNotNull(resultado.get(0).getStatus());
    }

    @Test
    void deveLancarErroQuandoFkUsuarioNuloNaBuscaPorUsuario() {
        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(null));
    }

    @Test
    void deveFiltrarObjetivosConcluidosNaBuscaPorUsuario() {
        UUID usuarioId = UUID.randomUUID();
        Objetivo concluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().minusDays(1))
                .build();
        Objetivo naoConcluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().plusDays(1))
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(concluido, naoConcluido));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<Objetivo> resultado = useCase.execute(usuarioId, true);

        assertEquals(1, resultado.size());
        assertEquals(concluido.getId(), resultado.get(0).getId());
    }

    @Test
    void deveBuscarObjetivosPorNome() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        Objetivo obj = objetivoBase();
        when(objetivoRepository.findByNomeAndUsuario("delivery", usuarioId)).thenReturn(List.of(obj));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        // Act
        List<Objetivo> resultado = useCase.executeBuscarPorNome("delivery", usuarioId, null);

        // Assert
        assertEquals(1, resultado.size());
    }

    @Test
    void deveFiltrarObjetivosConcluidosNaBuscaPorNome() {
        UUID usuarioId = UUID.randomUUID();
        Objetivo concluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().minusDays(1))
                .build();
        Objetivo naoConcluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().plusDays(1))
                .build();

        when(objetivoRepository.findByNomeAndUsuario("delivery", usuarioId)).thenReturn(List.of(concluido, naoConcluido));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<Objetivo> resultado = useCase.executeBuscarPorNome("delivery", usuarioId, true);

        assertEquals(1, resultado.size());
        assertEquals(concluido.getId(), resultado.get(0).getId());
    }

    @Test
    void deveLancarErroQuandoNomeVazioNaBuscaPorNome() {
        assertThrows(ObjetivoInvalidoException.class,
                () -> useCase.executeBuscarPorNome("", UUID.randomUUID(), null));
    }

    @Test
    void deveLancarErroQuandoFkUsuarioNuloNaBuscaPorNome() {
        assertThrows(ObjetivoInvalidoException.class,
                () -> useCase.executeBuscarPorNome("teste", null, null));
    }

    @Test
    void deveFiltrarObjetivosNaoConcluidosNaBuscaPorUsuario() {
        UUID usuarioId = UUID.randomUUID();
        Objetivo concluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().minusDays(1))
                .build();
        Objetivo naoConcluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().plusDays(1))
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(concluido, naoConcluido));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<Objetivo> resultado = useCase.execute(usuarioId, false);

        assertEquals(1, resultado.size());
        assertEquals(naoConcluido.getId(), resultado.get(0).getId());
    }

    @Test
    void deveLancarErroQuandoNomeNuloNaBuscaPorNome() {
        assertThrows(ObjetivoInvalidoException.class,
                () -> useCase.executeBuscarPorNome(null, UUID.randomUUID(), null));
    }

    @Test
    void deveFiltrarObjetivosNaoConcluidosNaBuscaPorNome() {
        UUID usuarioId = UUID.randomUUID();
        Objetivo concluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().minusDays(1))
                .build();
        Objetivo naoConcluido = objetivoBase().toBuilder()
                .dataFim(LocalDate.now().plusDays(1))
                .build();

        when(objetivoRepository.findByNomeAndUsuario("delivery", usuarioId)).thenReturn(List.of(concluido, naoConcluido));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<Objetivo> resultado = useCase.executeBuscarPorNome("delivery", usuarioId, false);

        assertEquals(1, resultado.size());
        assertEquals(naoConcluido.getId(), resultado.get(0).getId());
    }

    @Test
    void deveLancarErroQuandoFkUsuarioNuloNaBuscaComConcluido() {
        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(null, false));
    }

    @Test
    void deveOrdenarObjetivosPorPrioridadeNaListagem() {
        UUID usuarioId = UUID.randomUUID();
        Objetivo semPrioridade = objetivoBase().toBuilder()
                .nome("Sem prioridade")
                .prioridade(null)
                .build();
        Objetivo baixa = objetivoBase().toBuilder()
                .nome("Baixa")
                .prioridade(PrioridadeObjetivo.BAIXA)
                .build();
        Objetivo alta = objetivoBase().toBuilder()
                .nome("Alta")
                .prioridade(PrioridadeObjetivo.ALTA)
                .build();
        Objetivo media = objetivoBase().toBuilder()
                .nome("Media")
                .prioridade(PrioridadeObjetivo.MEDIA)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId))
                .thenReturn(List.of(semPrioridade, baixa, alta, media));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<Objetivo> resultado = useCase.execute(usuarioId);

        assertEquals("Alta", resultado.get(0).getNome());
        assertEquals("Media", resultado.get(1).getNome());
        assertEquals("Baixa", resultado.get(2).getNome());
        assertEquals("Sem prioridade", resultado.get(3).getNome());
    }
}
