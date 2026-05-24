package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.dto.categoria.GastoPorCategoriaResponse;
import br.com.contadin.application.dto.transacao.GastoCategoriaAgregado;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.enums.TipoInstituicao;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Instituicao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarGastoPorCategoriaUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private BuscarGastoPorCategoriaUseCase useCase;

    private UUID fkUsuario;
    private UUID fkInstituicao;
    private UUID fkCategoria;
    private Instituicao instituicao;
    private Categoria categoria;

    @BeforeEach
    void setup() {
        fkUsuario = UUID.randomUUID();
        fkInstituicao = UUID.randomUUID();
        fkCategoria = UUID.randomUUID();

        instituicao = Instituicao.builder()
                .id(fkInstituicao)
                .nome("Nubank")
                .tipo(TipoInstituicao.BANCO)
                .fkUsuario(fkUsuario)
                .ativo(true)
                .build();

        categoria = Categoria.builder()
                .id(fkCategoria)
                .nome("Alimentação")
                .icone("fork")
                .cor("#FF5733")
                .tipo(TipoCategoria.GASTO)
                .fkUsuario(fkUsuario)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve buscar gastos usando a instituição informada diretamente")
        void deveBuscarGastosComInstituicaoEspecifica() {
            List<GastoCategoriaAgregado> agregados = List.of(
                    new GastoCategoriaAgregado(fkCategoria, new BigDecimal("200.00"))
            );

            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(agregados);

            when(categoriaRepository.findByIdIncludingInactive(fkCategoria))
                    .thenReturn(Optional.of(categoria));

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, fkInstituicao);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());

            verify(instituicaoRepository, never()).findAtivasByUsuario(any());
        }

        @Test
        @DisplayName("Deve buscar todas as instituições ativas quando fkInstituicao for nulo")
        void deveBuscarTodasInstituicoesQuandoInstituicaoNula() {
            List<GastoCategoriaAgregado> agregados = List.of(
                    new GastoCategoriaAgregado(fkCategoria, new BigDecimal("150.00"))
            );

            when(instituicaoRepository.findAtivasByUsuario(fkUsuario))
                    .thenReturn(List.of(instituicao));

            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(agregados);

            when(categoriaRepository.findByIdIncludingInactive(fkCategoria))
                    .thenReturn(Optional.of(categoria));

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, null);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());

            verify(instituicaoRepository).findAtivasByUsuario(fkUsuario);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver instituições ativas")
        void deveRetornarListaVaziaQuandoSemInstituicoes() {
            when(instituicaoRepository.findAtivasByUsuario(fkUsuario))
                    .thenReturn(List.of());

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, null);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());

            verify(transacaoRepository, never()).buscarGastoPorCategoria(any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver gastos no período")
        void deveRetornarListaVaziaQuandoSemGastos() {
            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, fkInstituicao);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve calcular percentual corretamente entre categorias")
        void deveCalcularPercentualCorretamente() {
            UUID fkCat2 = UUID.randomUUID();

            List<GastoCategoriaAgregado> agregados = List.of(
                    new GastoCategoriaAgregado(fkCategoria, new BigDecimal("300.00")),
                    new GastoCategoriaAgregado(fkCat2, new BigDecimal("700.00"))
            );

            Categoria categoria2 = Categoria.builder()
                    .id(fkCat2).nome("Transporte").icone("car").cor("#0000FF")
                    .tipo(TipoCategoria.GASTO).fkUsuario(fkUsuario).build();

            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(agregados);

            when(categoriaRepository.findByIdIncludingInactive(fkCategoria))
                    .thenReturn(Optional.of(categoria));

            when(categoriaRepository.findByIdIncludingInactive(fkCat2))
                    .thenReturn(Optional.of(categoria2));

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, fkInstituicao);

            assertEquals(2, resultado.size());

            GastoPorCategoriaResponse item1 = resultado.get(0);
            GastoPorCategoriaResponse item2 = resultado.get(1);

            assertEquals(new BigDecimal("30.00"), item1.percentual());
            assertEquals(new BigDecimal("70.00"), item2.percentual());
        }

        @Test
        @DisplayName("Deve retornar 'Sem Categoria' e campos nulos quando fkCategoria do agregado for nulo")
        void deveRetornarSemCategoriaQuandoFkCategoriaNulo() {
            List<GastoCategoriaAgregado> agregados = List.of(
                    new GastoCategoriaAgregado(null, new BigDecimal("100.00"))
            );

            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(agregados);

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, fkInstituicao);

            assertEquals(1, resultado.size());

            GastoPorCategoriaResponse item = resultado.get(0);
            assertNull(item.fkCategoria());
            assertEquals("Sem Categoria", item.nome());
            assertNull(item.icone());
            assertNull(item.cor());
        }

        @Test
        @DisplayName("Deve retornar 'Sem Categoria' quando categoria não for encontrada no repositório")
        void deveRetornarSemCategoriaQuandoCategoriaForNull() {
            List<GastoCategoriaAgregado> agregados = List.of(
                    new GastoCategoriaAgregado(fkCategoria, new BigDecimal("100.00"))
            );

            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(agregados);

            when(categoriaRepository.findByIdIncludingInactive(fkCategoria))
                    .thenReturn(Optional.empty());

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, fkInstituicao);

            assertEquals(1, resultado.size());
            assertEquals("Sem Categoria", resultado.get(0).nome());
        }

        @Test
        @DisplayName("Deve preencher dados completos da categoria quando encontrada")
        void devePreencherDadosDaCategoriaQuandoEncontrada() {
            List<GastoCategoriaAgregado> agregados = List.of(
                    new GastoCategoriaAgregado(fkCategoria, new BigDecimal("250.00"))
            );

            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(agregados);

            when(categoriaRepository.findByIdIncludingInactive(fkCategoria))
                    .thenReturn(Optional.of(categoria));

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, fkInstituicao);

            assertEquals(1, resultado.size());

            GastoPorCategoriaResponse item = resultado.get(0);
            assertEquals(fkCategoria, item.fkCategoria());
            assertEquals("Alimentação", item.nome());
            assertEquals("fork", item.icone());
            assertEquals("#FF5733", item.cor());
            assertEquals(new BigDecimal("250.00"), item.total());
            assertEquals(new BigDecimal("100.00"), item.percentual());
        }

        @Test
        @DisplayName("Deve retornar percentual zero quando total geral for zero")
        void deveRetornarPercentualZeroQuandoTotalGeralForZero() {
            List<GastoCategoriaAgregado> agregados = List.of(
                    new GastoCategoriaAgregado(fkCategoria, BigDecimal.ZERO)
            );

            when(transacaoRepository.buscarGastoPorCategoria(
                    eq(List.of(fkInstituicao)), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(agregados);

            when(categoriaRepository.findByIdIncludingInactive(fkCategoria))
                    .thenReturn(Optional.of(categoria));

            List<GastoPorCategoriaResponse> resultado = useCase.execute(fkUsuario, 5, 2026, fkInstituicao);

            assertEquals(BigDecimal.ZERO, resultado.get(0).percentual());
        }
    }
}
