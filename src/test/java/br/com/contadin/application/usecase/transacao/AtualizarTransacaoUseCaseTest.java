package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarTransacaoUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SaldoDiarioCalculadorService calculadorService;

    @InjectMocks
    private AtualizarTransacaoUseCase useCase;

    private UUID transacaoId;
    private UUID fkInstituicao;
    private Transacao existente;

    @BeforeEach
    void setup() {
        transacaoId = UUID.randomUUID();
        fkInstituicao = UUID.randomUUID();

        existente = Transacao.builder()
                .id(transacaoId)
                .valor(100.0)
                .tipo(TipoTransacao.GASTO)
                .descricao("Descricao antiga")
                .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                .parcelado(false)
                .qtdParcelas(null)
                .ativo(true)
                .fkInstituicao(fkInstituicao)
                .fkCategoria(UUID.randomUUID())
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve atualizar campos informados no patch")
        void deveAtualizarCamposInformados() {
            UUID novaInstituicao = fkInstituicao;
            Transacao patch = Transacao.builder()
                    .valor(200.0)
                    .tipo(TipoTransacao.RECEITA)
                    .descricao("Nova descrição")
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(novaInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoId, patch);

            verify(transacaoRepository).save(captor.capture());

            Transacao salva = captor.getValue();
            assertEquals(200.0, salva.getValor());
            assertEquals(TipoTransacao.RECEITA, salva.getTipo());
            assertEquals("Nova descrição", salva.getDescricao());
        }

        @Test
        @DisplayName("Deve manter valores existentes quando campos do patch forem nulos")
        void deveMantermValoresExistentesQuandoPatchForNulo() {
            Transacao patch = Transacao.builder()
                    .valor(null)
                    .tipo(null)
                    .descricao(null)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoId, patch);

            verify(transacaoRepository).save(captor.capture());

            Transacao salva = captor.getValue();
            assertEquals(existente.getValor(), salva.getValor());
            assertEquals(existente.getTipo(), salva.getTipo());
            assertEquals(existente.getDescricao(), salva.getDescricao());
        }

        @Test
        @DisplayName("Deve anular qtdParcelas quando parcelado for alterado para false")
        void deveAnularQtdParcelasQuandoParceladoFalse() {
            Transacao parcelada = Transacao.builder()
                    .id(transacaoId)
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(12)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            Transacao patch = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(parcelada));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoId, patch);

            verify(transacaoRepository).save(captor.capture());
            assertNull(captor.getValue().getQtdParcelas());
        }

        @Test
        @DisplayName("Deve recalcular usando a data mais antiga quando data for alterada para frente")
        void deveRecalcularComDataMaisAntigaQuandoDataAvanca() {
            Transacao patch = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 20, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            useCase.execute(transacaoId, patch);

            // Data anterior (10/05) é menor → recalcula a partir de 10/05
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 10));
        }

        @Test
        @DisplayName("Deve recalcular usando a data mais antiga quando data for alterada para trás")
        void deveRecalcularComDataMaisAntigaQuandoDataRetrocede() {
            Transacao patch = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 5, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            useCase.execute(transacaoId, patch);

            // Nova data (05/05) é menor → recalcula a partir de 05/05
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 5));
        }

        @Test
        @DisplayName("Deve recalcular ambas as instituições quando instituição for alterada")
        void deveRecalcularAmbasInstituicoesQuandoInstituicaoMuda() {
            UUID novaInstituicao = UUID.randomUUID();

            Transacao patch = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(novaInstituicao)
                    .build();

            Transacao salva = Transacao.builder()
                    .id(transacaoId)
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .ativo(true)
                    .fkInstituicao(novaInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenReturn(salva);

            useCase.execute(transacaoId, patch);

            // Recalcula nova instituição
            verify(calculadorService).recalcular(novaInstituicao, LocalDate.of(2026, 5, 10));
            // Recalcula instituição anterior
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 10));
            verify(calculadorService, times(2)).recalcular(any(), any());
        }

        @Test
        @DisplayName("Deve recalcular apenas uma vez quando instituição não muda")
        void deveRecalcularApenasUmaVezQuandoInstituicaoNaoMuda() {
            Transacao patch = Transacao.builder()
                    .valor(200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            useCase.execute(transacaoId, patch);

            verify(calculadorService, times(1)).recalcular(any(), any());
        }

        @Test
        @DisplayName("Deve retornar com sucesso mesmo quando recálculo falhar")
        void deveRetornarMesmoQuandoRecalcularFalhar() {
            Transacao patch = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));
            doThrow(new RuntimeException("Erro no recálculo")).when(calculadorService).recalcular(any(), any());

            assertDoesNotThrow(() -> useCase.execute(transacaoId, patch));
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar TransacaoInvalidaException quando ID for nulo")
        void deveLancarExcecaoQuandoIdNulo() {
            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(null, Transacao.builder().build())
            );

            assertEquals("ID da transação é obrigatório para atualização.", ex.getMessage());
            verify(transacaoRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Deve lançar TransacaoNaoEncontradaException quando transação não existir")
        void deveLancarExcecaoQuandoTransacaoNaoExistir() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.empty());

            assertThrows(
                    TransacaoNaoEncontradaException.class,
                    () -> useCase.execute(transacaoId, Transacao.builder()
                            .valor(100.0)
                            .tipo(TipoTransacao.GASTO)
                            .dataTransacao(LocalDateTime.now())
                            .parcelado(false)
                            .fkInstituicao(fkInstituicao)
                            .build())
            );

            verify(transacaoRepository, never()).save(any());
        }
    }

    @Nested
    class ValidacaoAposAtualizacao {

        private Transacao buildPatch(Double valor, TipoTransacao tipo, LocalDateTime data, Boolean parcelado, Integer qtd) {
            return Transacao.builder()
                    .valor(valor)
                    .tipo(tipo)
                    .dataTransacao(data)
                    .parcelado(parcelado)
                    .qtdParcelas(qtd)
                    .fkInstituicao(fkInstituicao)
                    .build();
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor resultante for nulo")
        void deveLancarExcecaoParaValorNulo() {
            Transacao semValor = Transacao.builder()
                    .id(transacaoId)
                    .valor(null)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(semValor));

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(transacaoId, buildPatch(null, null, LocalDateTime.of(2026, 5, 10, 10, 0), false, null))
            );

            assertEquals("O valor da transação deve ser maior que zero.", ex.getMessage());
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando parcelado=true e qtdParcelas < 2 após merge")
        void deveLancarExcecaoParaQtdParcelasAbaixoDoMinimo() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));

            Transacao patch = buildPatch(100.0, TipoTransacao.GASTO, LocalDateTime.of(2026, 5, 10, 10, 0), true, 1);

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(transacaoId, patch)
            );

            assertEquals("A quantidade de parcelas deve estar entre 2 e 720.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando parcelado=true e qtdParcelas > 720 após merge")
        void deveLancarExcecaoParaQtdParcelasAcimaDoMaximo() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));

            Transacao patch = buildPatch(100.0, TipoTransacao.GASTO, LocalDateTime.of(2026, 5, 10, 10, 0), true, 721);

            assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(transacaoId, patch));
        }

        @Test
        @DisplayName("Deve lançar exceção quando parcelado resultar nulo após merge")
        void deveLancarExcecaoQuandoParceladoNuloAposMerge() {
            Transacao semParcelado = Transacao.builder()
                    .id(transacaoId)
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(null)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(semParcelado));

            Transacao patch = buildPatch(100.0, TipoTransacao.GASTO, LocalDateTime.of(2026, 5, 10, 10, 0), null, null);

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(transacaoId, patch)
            );
            assertEquals("O campo parcelado é obrigatório.", ex.getMessage());
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando parcelado=true e qtdParcelas nula após merge")
        void deveLancarExcecaoQuandoParceladoTrueEQtdParcelasNula() {
            Transacao semQtd = Transacao.builder()
                    .id(transacaoId)
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .qtdParcelas(null)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(semQtd));

            Transacao patch = buildPatch(100.0, TipoTransacao.GASTO, LocalDateTime.of(2026, 5, 10, 10, 0), true, null);

            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(transacaoId, patch)
            );
            assertEquals("A quantidade de parcelas é obrigatória para transações parceladas.", ex.getMessage());
        }
    }

    @Nested
    class RecalculoComInstituicaoMudandoEFalhando {

        @Test
        @DisplayName("Deve continuar mesmo quando ambos os recalculos falharem ao mudar instituição")
        void deveRetornarMesmoQuandoAmbosRecalcularemFalharem() {
            UUID novaInstituicao = UUID.randomUUID();

            Transacao patch = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(novaInstituicao)
                    .build();

            Transacao salva = Transacao.builder()
                    .id(transacaoId)
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .ativo(true)
                    .fkInstituicao(novaInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));
            when(transacaoRepository.save(any(Transacao.class))).thenReturn(salva);
            doThrow(new RuntimeException("Erro no recálculo")).when(calculadorService).recalcular(any(), any());

            assertDoesNotThrow(() -> useCase.execute(transacaoId, patch));
        }
    }

    @Nested
    @DisplayName("Cenários de atualização de transações parceladas")
    class CenariosDeParcelado {

        private Transacao existenteParcelado;

        @BeforeEach
        void setupParcelado() {
            existenteParcelado = Transacao.builder()
                    .id(transacaoId)
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .descricao("Celular parcelado")
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(12)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();
        }

        @Test
        @DisplayName("Deve alterar qtdParcelas e recalcular a partir da data da primeira parcela")
        void deveAlterarQtdParcelas_eRecalcular() {
            Transacao patch = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(6)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existenteParcelado));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
            useCase.execute(transacaoId, patch);
            verify(transacaoRepository).save(captor.capture());

            assertEquals(6, captor.getValue().getQtdParcelas());
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 10));
        }

        @Test
        @DisplayName("Deve alterar valor total da parcelada e recalcular")
        void deveAlterarValorTotal_eRecalcular() {
            Transacao patch = Transacao.builder()
                    .valor(600.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(12)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existenteParcelado));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
            useCase.execute(transacaoId, patch);
            verify(transacaoRepository).save(captor.capture());

            assertEquals(600.0, captor.getValue().getValor());
            assertEquals(12, captor.getValue().getQtdParcelas());
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 10));
        }

        @Test
        @DisplayName("Deve recalcular a partir da data mais antiga ao mover data da primeira parcela")
        void deveRecalcularDataMaisAntiga_aoAlterarDataPrimeiraParcela() {
            Transacao patch = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 1, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(12)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existenteParcelado));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            useCase.execute(transacaoId, patch);

            // Data anterior era 10/05; nova é 01/05 → recalcula da mais antiga (01/05)
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 1));
        }

        @Test
        @DisplayName("Deve rejeitar qtdParcelas=1 ao atualizar transação parcelada")
        void deveRejeitar_qtdParcelas1_aoAtualizar() {
            Transacao patch = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(1)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existenteParcelado));

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(transacaoId, patch));

            assertEquals("A quantidade de parcelas deve estar entre 2 e 720.", ex.getMessage());
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve rejeitar qtdParcelas=0 ao atualizar transação parcelada")
        void deveRejeitar_qtdParcelas0_aoAtualizar() {
            Transacao patch = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(0)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existenteParcelado));

            assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(transacaoId, patch));
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve converter parcelada para pontual, zerando qtdParcelas")
        void deveConverterParceladaParaPontual_zerandoQtdParcelas() {
            Transacao patch = Transacao.builder()
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(false)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existenteParcelado));
            when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
            useCase.execute(transacaoId, patch);
            verify(transacaoRepository).save(captor.capture());

            assertFalse(captor.getValue().getParcelado());
            assertNull(captor.getValue().getQtdParcelas());
            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 10));
        }

        @Test
        @DisplayName("Deve rejeitar conversão de pontual para parcelada sem informar qtdParcelas")
        void deveRejeitar_converterParaParcela_semQtdParcelas() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(existente));

            Transacao patch = Transacao.builder()
                    .valor(100.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 10, 10, 0))
                    .parcelado(true)
                    .qtdParcelas(null)
                    .fkInstituicao(fkInstituicao)
                    .build();

            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(transacaoId, patch));

            assertEquals("A quantidade de parcelas é obrigatória para transações parceladas.", ex.getMessage());
            verify(transacaoRepository, never()).save(any());
        }
    }
}
