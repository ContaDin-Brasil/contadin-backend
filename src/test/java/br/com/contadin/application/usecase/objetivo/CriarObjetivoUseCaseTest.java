package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Objetivo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarObjetivoUseCaseTest {

    @Mock
    private ObjetivoRepository objetivoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CriarObjetivoUseCase useCase;

    @Test
    void deveCriarObjetivoQuandoDadosValidos() {
        // Arrange
        UUID categoriaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        Categoria categoria = Categoria.builder()
                .id(categoriaId)
                .tipo(TipoCategoria.GASTO)
                .build();

        Objetivo input = Objetivo.builder()
                .nome("Limitar delivery")
                .valor(BigDecimal.valueOf(450))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .prioridade(PrioridadeObjetivo.ALTA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(categoriaId)
                .build();

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(objetivoRepository.save(any(Objetivo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(
                eq(categoriaId), eq(TipoTransacao.GASTO), any(), any()))
                .thenReturn(BigDecimal.valueOf(200));

        // Act
        Objetivo resultado = useCase.execute(input);

        // Assert
        assertNotNull(resultado);
        assertEquals("Limitar delivery", resultado.getNome());
        assertEquals(BigDecimal.valueOf(200), resultado.getRealizado());
        assertNotNull(resultado.getStatus());
        verify(objetivoRepository).save(any(Objetivo.class));
    }

    @Test
    void deveUsarDataAtualComoInicioQuandoDataInicioNula() {
        // Arrange
        UUID categoriaId = UUID.randomUUID();
        Categoria categoria = Categoria.builder().id(categoriaId).tipo(TipoCategoria.GASTO).build();

        Objetivo input = Objetivo.builder()
                .nome("Teste")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataFim(LocalDate.of(2026, 12, 31))
                .fkUsuario(UUID.randomUUID())
                .fkCategoria(categoriaId)
                .build();

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(objetivoRepository.save(any(Objetivo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        // Act
        Objetivo resultado = useCase.execute(input);

        // Assert
        assertEquals(LocalDate.now(), resultado.getDataInicio());
    }

    @Test
    void deveLancarErroQuandoValorNulo() {
        Objetivo input = Objetivo.builder()
                .nome("Teste")
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(UUID.randomUUID())
                .fkCategoria(UUID.randomUUID())
                .build();

        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(input));
    }

    @Test
    void deveLancarErroQuandoValorZeroOuNegativo() {
        Objetivo input = Objetivo.builder()
                .nome("Teste")
                .valor(BigDecimal.ZERO)
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(UUID.randomUUID())
                .fkCategoria(UUID.randomUUID())
                .build();

        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(input));
    }

    @Test
    void deveLancarErroQuandoDataFimAnteriorADataInicio() {
        Objetivo input = Objetivo.builder()
                .nome("Teste")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 6, 1))
                .dataFim(LocalDate.of(2026, 5, 1))
                .fkUsuario(UUID.randomUUID())
                .fkCategoria(UUID.randomUUID())
                .build();

        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(input));
    }

    @Test
    void deveLancarErroQuandoCategoriaNaoEncontrada() {
        UUID categoriaId = UUID.randomUUID();
        Objetivo input = Objetivo.builder()
                .nome("Teste")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(UUID.randomUUID())
                .fkCategoria(categoriaId)
                .build();

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(input));
    }

    @Test
    void deveLancarErroQuandoCategoriaIncompativelComTipo() {
        UUID categoriaId = UUID.randomUUID();
        Categoria categoriaReceita = Categoria.builder()
                .id(categoriaId)
                .tipo(TipoCategoria.RECEITA)
                .build();

        Objetivo input = Objetivo.builder()
                .nome("Teste")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(UUID.randomUUID())
                .fkCategoria(categoriaId)
                .build();

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaReceita));

        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(input));
    }
}
