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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarTransacaoUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SaldoDiarioCalculadorService calculadorService;

    @InjectMocks
    private CriarTransacaoUseCase useCase;

    private UUID fkInstituicao;
    private Transacao transacaoValida;

    @BeforeEach
    void setup() {
        fkInstituicao = UUID.randomUUID();

        transacaoValida = Transacao.builder()
                .valor(150.0)
                .tipo(TipoTransacao.GASTO)
                .descricao("Supermercado")
                .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                .parcelado(false)
                .fkInstituicao(fkInstituicao)
                .fkCategoria(UUID.randomUUID())
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve criar transação e retornar o objeto salvo")
        void deveCriarTransacaoComSucesso() {
            Transacao salva = Transacao.builder()
                    .id(UUID.randomUUID())
                    .valor(150.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(transacaoValida.getDataTransacao())
                    .parcelado(false)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenReturn(salva);

            Transacao resultado = useCase.execute(transacaoValida);

            assertNotNull(resultado);
            assertEquals(salva.getId(), resultado.getId());
        }

        @Test
        @DisplayName("Deve definir ativo=true quando não informado")
        void deveDefinirAtivoTrueQuandoNaoInformado() {
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoValida);

            verify(transacaoRepository).save(captor.capture());
            assertTrue(captor.getValue().getAtivo());
        }

        @Test
        @DisplayName("Deve preservar ativo=false quando explicitamente informado")
        void devePreservarAtivoFalseQuandoInformado() {
            Transacao inativa = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.RECEITA)
                    .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                    .parcelado(false)
                    .ativo(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(inativa);

            verify(transacaoRepository).save(captor.capture());
            assertFalse(captor.getValue().getAtivo());
        }

        @Test
        @DisplayName("Deve anular qtdParcelas quando parcelado=false")
        void deveAnularQtdParcelasQuandoNaoParcelado() {
            Transacao comQtdIndevida = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                    .parcelado(false)
                    .qtdParcelas(12)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(comQtdIndevida);

            verify(transacaoRepository).save(captor.capture());
            assertNull(captor.getValue().getQtdParcelas());
        }

        @Test
        @DisplayName("Deve manter qtdParcelas quando parcelado=true")
        void deveManterQtdParcelasQuandoParcelado() {
            Transacao parcelada = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(12)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(parcelada);

            verify(transacaoRepository).save(captor.capture());
            assertEquals(12, captor.getValue().getQtdParcelas());
        }

        @Test
        @DisplayName("Deve disparar recálculo do saldo diário após salvar")
        void deveDispararRecalculo() {
            Transacao salva = Transacao.builder()
                    .id(UUID.randomUUID())
                    .valor(150.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                    .parcelado(false)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenReturn(salva);

            useCase.execute(transacaoValida);

            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 15));
        }

        @Test
        @DisplayName("Deve criar com sucesso mesmo quando recálculo lançar exceção")
        void deveCriarMesmoQuandoRecalcularFalhar() {
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> {
                Transacao t = inv.getArgument(0);
                return Transacao.builder()
                        .id(UUID.randomUUID())
                        .valor(t.getValor())
                        .tipo(t.getTipo())
                        .dataTransacao(t.getDataTransacao())
                        .parcelado(t.getParcelado())
                        .ativo(t.getAtivo())
                        .fkInstituicao(t.getFkInstituicao())
                        .build();
            });
            doThrow(new RuntimeException("Erro no recálculo")).when(calculadorService).recalcular(any(), any());

            assertDoesNotThrow(() -> useCase.execute(transacaoValida));
        }
    }

    @Nested
    class ValidacaoDeValor {

        @Test
        @DisplayName("Deve lançar exceção quando valor for nulo")
        void deveLancarExcecaoParaValorNulo() {
            Transacao invalida = Transacao.builder()
                    .valor(null)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(invalida));

            assertEquals("O valor da transação deve ser maior que zero.", ex.getMessage());
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor for zero")
        void deveLancarExcecaoParaValorZero() {
            Transacao invalida = Transacao.builder()
                    .valor(0.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(invalida));
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor for negativo")
        void deveLancarExcecaoParaValorNegativo() {
            Transacao invalida = Transacao.builder()
                    .valor(-50.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(invalida));
        }
    }

    @Nested
    class ValidacaoDeCamposObrigatorios {

        @Test
        @DisplayName("Deve lançar exceção quando tipo for nulo")
        void deveLancarExcecaoParaTipoNulo() {
            Transacao invalida = Transacao.builder()
                    .valor(100.0)
                    .tipo(null)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(invalida));

            assertEquals("O tipo da transação é obrigatório.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando dataTransacao for nula")
        void deveLancarExcecaoParaDataNula() {
            Transacao invalida = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(null)
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(invalida));

            assertEquals("A data da transação é obrigatória.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando parcelado for nulo")
        void deveLancarExcecaoParaParceladoNulo() {
            Transacao invalida = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(null)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(invalida));

            assertEquals("O campo parcelado é obrigatório.", ex.getMessage());
        }
    }

    @Nested
    class ValidacaoDeParcelamento {

        @Test
        @DisplayName("Deve lançar exceção quando parcelado=true e qtdParcelas for nula")
        void deveLancarExcecaoParaQtdParcelasNula() {
            Transacao invalida = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(true)
                    .qtdParcelas(null)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(invalida));

            assertEquals("A quantidade de parcelas é obrigatória para transações parceladas.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando qtdParcelas for menor que 2")
        void deveLancarExcecaoParaQtdParcelasMenorQue2() {
            Transacao invalida = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(true)
                    .qtdParcelas(1)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(invalida));

            assertEquals("A quantidade de parcelas deve estar entre 2 e 720.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando qtdParcelas for maior que 720")
        void deveLancarExcecaoParaQtdParcelasMaiorQue720() {
            Transacao invalida = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(true)
                    .qtdParcelas(721)
                    .fkInstituicao(fkInstituicao)
                    .build();

            assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(invalida));
        }

        @Test
        @DisplayName("Deve lançar exceção quando qtdParcelas for zero")
        void deveLancarExcecaoParaQtdParcelasZero() {
            Transacao invalida = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(true)
                    .qtdParcelas(0)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(invalida));

            assertEquals("A quantidade de parcelas deve estar entre 2 e 720.", ex.getMessage());
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando qtdParcelas for negativa")
        void deveLancarExcecaoParaQtdParcelasNegativa() {
            Transacao invalida = Transacao.builder()
                    .valor(500.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.now())
                    .parcelado(true)
                    .qtdParcelas(-5)
                    .fkInstituicao(fkInstituicao)
                    .build();

            assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(invalida));
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve aceitar qtdParcelas nos limites exatos: 2 e 720")
        void deveAceitarQtdParcelasNosLimites() {
            Transacao parcelada2 = Transacao.builder()
                    .valor(200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(2)
                    .fkInstituicao(fkInstituicao)
                    .build();

            Transacao parcelada720 = Transacao.builder()
                    .valor(720.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(720)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            assertDoesNotThrow(() -> useCase.execute(parcelada2));
            assertDoesNotThrow(() -> useCase.execute(parcelada720));
        }
    }

    @Nested
    @DisplayName("Inserção de transações parceladas — cenários adversos")
    class InsercaoDeParcelados {

        @Test
        @DisplayName("Deve disparar recálculo a partir da data da primeira parcela")
        void deveDispararRecalculo_naDataDaPrimeiraParcela() {
            LocalDateTime dataPrimeiraParcela = LocalDateTime.of(2026, 8, 10, 0, 0);
            Transacao parcelada = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(dataPrimeiraParcela)
                    .parcelado(true)
                    .qtdParcelas(12)
                    .fkInstituicao(fkInstituicao)
                    .build();

            Transacao salva = Transacao.builder()
                    .id(UUID.randomUUID())
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(dataPrimeiraParcela)
                    .parcelado(true)
                    .qtdParcelas(12)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenReturn(salva);

            useCase.execute(parcelada);

            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 8, 10));
        }

        @Test
        @DisplayName("Deve salvar qtdParcelas e parcelado=true intactos")
        void deveSalvarCamposParceladoIntactos() {
            Transacao parcelada = Transacao.builder()
                    .valor(500.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 7, 1, 0, 0))
                    .parcelado(true)
                    .qtdParcelas(5)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
            useCase.execute(parcelada);
            verify(transacaoRepository).save(captor.capture());

            assertEquals(5, captor.getValue().getQtdParcelas());
            assertTrue(captor.getValue().getParcelado());
        }

        @Test
        @DisplayName("Deve aceitar valor fracionário pequeno em parcelado")
        void deveAceitarValorFracionarioPequeno() {
            Transacao parcelada = Transacao.builder()
                    .valor(0.02)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 7, 1, 0, 0))
                    .parcelado(true)
                    .qtdParcelas(2)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            assertDoesNotThrow(() -> useCase.execute(parcelada));
        }
    }
}
