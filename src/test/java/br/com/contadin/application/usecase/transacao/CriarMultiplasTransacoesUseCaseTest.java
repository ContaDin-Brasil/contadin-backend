package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarMultiplasTransacoesUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SaldoDiarioCalculadorService calculadorService;

    @InjectMocks
    private CriarMultiplasTransacoesUseCase useCase;

    private UUID fkInstituicao;

    @BeforeEach
    void setup() {
        fkInstituicao = UUID.randomUUID();
    }

    private Transacao transacaoValida(UUID instituicao, LocalDateTime data) {
        return Transacao.builder()
                .valor(100.0)
                .tipo(TipoTransacao.GASTO)
                .descricao("Teste")
                .dataTransacao(data)
                .parcelado(false)
                .fkInstituicao(instituicao)
                .fkCategoria(UUID.randomUUID())
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve salvar todas as transações e retornar a lista salva")
        void deveSalvarTodasAsTransacoes() {
            List<Transacao> entrada = List.of(
                    transacaoValida(fkInstituicao, LocalDateTime.of(2026, 5, 10, 10, 0)),
                    transacaoValida(fkInstituicao, LocalDateTime.of(2026, 5, 12, 10, 0))
            );

            List<Transacao> salvas = List.of(
                    Transacao.builder().id(UUID.randomUUID()).valor(100.0).tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0)).parcelado(false).ativo(true).fkInstituicao(fkInstituicao).build(),
                    Transacao.builder().id(UUID.randomUUID()).valor(100.0).tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 12, 10, 0)).parcelado(false).ativo(true).fkInstituicao(fkInstituicao).build()
            );

            when(transacaoRepository.saveAll(anyList())).thenReturn(salvas);

            List<Transacao> resultado = useCase.execute(entrada);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Deve definir ativo=true quando não informado em nenhuma transação")
        void deveDefinirAtivoTrueQuandoNaoInformado() {
            List<Transacao> entrada = List.of(
                    transacaoValida(fkInstituicao, LocalDateTime.of(2026, 5, 10, 10, 0))
            );

            when(transacaoRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<List<Transacao>> captor = ArgumentCaptor.forClass(List.class);

            useCase.execute(entrada);

            verify(transacaoRepository).saveAll(captor.capture());
            assertTrue(captor.getValue().get(0).getAtivo());
        }

        @Test
        @DisplayName("Deve anular qtdParcelas quando parcelado=false")
        void deveAnularQtdParcelasQuandoNaoParcelado() {
            Transacao comQtdIndevida = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .qtdParcelas(12)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<List<Transacao>> captor = ArgumentCaptor.forClass(List.class);

            useCase.execute(List.of(comQtdIndevida));

            verify(transacaoRepository).saveAll(captor.capture());
            assertNull(captor.getValue().get(0).getQtdParcelas());
        }

        @Test
        @DisplayName("Deve recalcular uma vez por instituição usando a data mais antiga do lote")
        void deveRecalcularUmaVezPorInstituicaoComDataMinima() {
            LocalDateTime dataAntiga = LocalDateTime.of(2026, 5, 5, 10, 0);
            LocalDateTime dataNova = LocalDateTime.of(2026, 5, 20, 10, 0);

            List<Transacao> entrada = List.of(
                    transacaoValida(fkInstituicao, dataNova),
                    transacaoValida(fkInstituicao, dataAntiga)
            );

            List<Transacao> salvas = List.of(
                    Transacao.builder().valor(100.0).tipo(TipoTransacao.GASTO).dataTransacao(dataNova)
                            .parcelado(false).ativo(true).fkInstituicao(fkInstituicao).build(),
                    Transacao.builder().valor(100.0).tipo(TipoTransacao.GASTO).dataTransacao(dataAntiga)
                            .parcelado(false).ativo(true).fkInstituicao(fkInstituicao).build()
            );

            when(transacaoRepository.saveAll(anyList())).thenReturn(salvas);

            useCase.execute(entrada);

            // Uma única chamada com a data mais antiga
            verify(calculadorService, times(1)).recalcular(any(), any());
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 5));
        }

        @Test
        @DisplayName("Deve recalcular separadamente para cada instituição distinta")
        void deveRecalcularPorInstituicaoDistinta() {
            UUID outraInstituicao = UUID.randomUUID();

            List<Transacao> entrada = List.of(
                    transacaoValida(fkInstituicao, LocalDateTime.of(2026, 5, 10, 10, 0)),
                    transacaoValida(outraInstituicao, LocalDateTime.of(2026, 5, 12, 10, 0))
            );

            List<Transacao> salvas = List.of(
                    Transacao.builder().valor(100.0).tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0)).parcelado(false).ativo(true).fkInstituicao(fkInstituicao).build(),
                    Transacao.builder().valor(100.0).tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 12, 10, 0)).parcelado(false).ativo(true).fkInstituicao(outraInstituicao).build()
            );

            when(transacaoRepository.saveAll(anyList())).thenReturn(salvas);

            useCase.execute(entrada);

            verify(calculadorService, times(2)).recalcular(any(), any());
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 10));
            verify(calculadorService).recalcular(outraInstituicao, LocalDate.of(2026, 5, 12));
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar TransacaoInvalidaException quando lista for nula")
        void deveLancarExcecaoParaListaNula() {
            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(null)
            );

            assertEquals("A lista de transações não pode ser vazia.", ex.getMessage());
            verify(transacaoRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar TransacaoInvalidaException quando lista estiver vazia")
        void deveLancarExcecaoParaListaVazia() {
            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(List.of())
            );

            assertEquals("A lista de transações não pode ser vazia.", ex.getMessage());
            verify(transacaoRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar exceção com posição da transação inválida no erro (valor nulo)")
        void deveLancarExcecaoComPosicaoParaValorNulo() {
            List<Transacao> lista = List.of(
                    transacaoValida(fkInstituicao, LocalDateTime.of(2026, 5, 10, 10, 0)),
                    Transacao.builder()
                            .valor(null)
                            .tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 11, 10, 0))
                            .parcelado(false)
                            .fkInstituicao(fkInstituicao)
                            .build()
            );

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(lista)
            );

            assertEquals("Transação 2: o valor deve ser maior que zero.", ex.getMessage());
            verify(transacaoRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar exceção com posição quando tipo for nulo")
        void deveLancarExcecaoComPosicaoParaTipoNulo() {
            List<Transacao> lista = List.of(
                    Transacao.builder()
                            .valor(100.0)
                            .tipo(null)
                            .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                            .parcelado(false)
                            .fkInstituicao(fkInstituicao)
                            .build()
            );

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(lista)
            );

            assertEquals("Transação 1: o tipo é obrigatório.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção com posição quando data for nula")
        void deveLancarExcecaoComPosicaoParaDataNula() {
            List<Transacao> lista = List.of(
                    Transacao.builder()
                            .valor(100.0)
                            .tipo(TipoTransacao.GASTO)
                            .dataTransacao(null)
                            .parcelado(false)
                            .fkInstituicao(fkInstituicao)
                            .build()
            );

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(lista)
            );

            assertEquals("Transação 1: a data é obrigatória.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando parcelado=true e qtdParcelas for nula")
        void deveLancarExcecaoParaParceladoSemQtd() {
            List<Transacao> lista = List.of(
                    Transacao.builder()
                            .valor(1200.0)
                            .tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                            .parcelado(true)
                            .qtdParcelas(null)
                            .fkInstituicao(fkInstituicao)
                            .build()
            );

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(lista)
            );

            assertEquals("Transação 1: a quantidade de parcelas é obrigatória para transações parceladas.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando qtdParcelas estiver fora do intervalo 2-720")
        void deveLancarExcecaoParaQtdParcelasForaDoIntervalo() {
            List<Transacao> listaAbaixo = List.of(
                    Transacao.builder()
                            .valor(100.0).tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                            .parcelado(true).qtdParcelas(1).fkInstituicao(fkInstituicao).build()
            );

            List<Transacao> listaAcima = List.of(
                    Transacao.builder()
                            .valor(100.0).tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                            .parcelado(true).qtdParcelas(721).fkInstituicao(fkInstituicao).build()
            );

            assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(listaAbaixo));
            assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(listaAcima));
        }

        @Test
        @DisplayName("Deve lançar exceção com posição quando parcelado for nulo")
        void deveLancarExcecaoComPosicaoParaParceladoNulo() {
            List<Transacao> lista = List.of(
                    Transacao.builder()
                            .valor(100.0)
                            .tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                            .parcelado(null)
                            .fkInstituicao(fkInstituicao)
                            .build()
            );

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(lista)
            );

            assertEquals("Transação 1: o campo parcelado é obrigatório.", ex.getMessage());
            verify(transacaoRepository, never()).saveAll(any());
        }
    }
}
