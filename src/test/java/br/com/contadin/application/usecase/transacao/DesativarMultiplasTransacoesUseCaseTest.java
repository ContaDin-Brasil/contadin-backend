package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.service.SaldoDiarioCalculadorService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesativarMultiplasTransacoesUseCaseTest {

    @Mock private TransacaoRepository transacaoRepository;
    @Mock private SaldoDiarioCalculadorService calculadorService;

    @InjectMocks
    private DesativarMultiplasTransacoesUseCase useCase;

    private UUID instId;
    private UUID id1, id2, id3;
    private Transacao t1, t2, t3;

    @BeforeEach
    void setup() {
        instId = UUID.randomUUID();
        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        id3 = UUID.randomUUID();

        t1 = transacao(id1, instId, LocalDateTime.of(2026, 5, 1, 0, 0), false, null);
        t2 = transacao(id2, instId, LocalDateTime.of(2026, 5, 10, 0, 0), false, null);
        t3 = transacao(id3, instId, LocalDateTime.of(2026, 5, 20, 0, 0), false, null);
    }

    private Transacao transacao(UUID id, UUID instId, LocalDateTime data, boolean parcelado, Integer qtdParcelas) {
        return Transacao.builder()
                .id(id)
                .valor(100.0)
                .tipo(TipoTransacao.GASTO)
                .dataTransacao(data)
                .parcelado(parcelado)
                .qtdParcelas(qtdParcelas)
                .ativo(true)
                .fkInstituicao(instId)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve salvar todas as transações com ativo=false")
        void deveDesativarTodasAsTransacoes() {
            when(transacaoRepository.findAllByIds(List.of(id1, id2, id3)))
                    .thenReturn(List.of(t1, t2, t3));
            when(transacaoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Transacao>> captor = ArgumentCaptor.forClass(List.class);

            useCase.execute(List.of(id1, id2, id3));

            verify(transacaoRepository).saveAll(captor.capture());
            captor.getValue().forEach(t -> assertFalse(t.getAtivo()));
        }

        @Test
        @DisplayName("Deve recalcular uma vez por instituição a partir da data mais antiga")
        void deveRecalcularUmaVezPorInstituicao() {
            when(transacaoRepository.findAllByIds(List.of(id1, id2, id3)))
                    .thenReturn(List.of(t1, t2, t3));
            when(transacaoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            useCase.execute(List.of(id1, id2, id3));

            verify(calculadorService, times(1)).recalcular(instId, t1.getDataTransacao().toLocalDate());
        }

        @Test
        @DisplayName("Deve recalcular uma vez por instituição para múltiplas instituições")
        void deveRecalcularPorInstituicaoDistinta() {
            UUID instId2 = UUID.randomUUID();
            Transacao tOutra = transacao(id3, instId2, LocalDateTime.of(2026, 4, 15, 0, 0), false, null);

            when(transacaoRepository.findAllByIds(List.of(id1, id3))).thenReturn(List.of(t1, tOutra));
            when(transacaoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            useCase.execute(List.of(id1, id3));

            verify(calculadorService, times(2)).recalcular(any(), any());
            verify(calculadorService).recalcular(instId, t1.getDataTransacao().toLocalDate());
            verify(calculadorService).recalcular(instId2, tOutra.getDataTransacao().toLocalDate());
        }

        @Test
        @DisplayName("Deve preservar qtdParcelas=null ao desativar transação não-parcelada")
        void devePreservarQtdParcelasNulaParaNaoParcelada() {
            when(transacaoRepository.findAllByIds(List.of(id1))).thenReturn(List.of(t1));
            when(transacaoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Transacao>> captor = ArgumentCaptor.forClass(List.class);

            useCase.execute(List.of(id1));

            verify(transacaoRepository).saveAll(captor.capture());
            assertEquals(null, captor.getValue().get(0).getQtdParcelas());
        }

        @Test
        @DisplayName("Deve preservar qtdParcelas ao desativar transação parcelada")
        void devePreservarQtdParcelasParaParcelada() {
            Transacao parcelada = transacao(id1, instId, LocalDateTime.of(2026, 5, 1, 0, 0), true, 12);

            when(transacaoRepository.findAllByIds(List.of(id1))).thenReturn(List.of(parcelada));
            when(transacaoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Transacao>> captor = ArgumentCaptor.forClass(List.class);

            useCase.execute(List.of(id1));

            verify(transacaoRepository).saveAll(captor.capture());
            assertEquals(12, captor.getValue().get(0).getQtdParcelas());
            assertFalse(captor.getValue().get(0).getAtivo());
        }

        @Test
        @DisplayName("Deve continuar mesmo quando recálculo falhar")
        void deveContinuarQuandoRecalcularFalhar() {
            when(transacaoRepository.findAllByIds(List.of(id1))).thenReturn(List.of(t1));
            when(transacaoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
            org.mockito.Mockito.doThrow(new RuntimeException("Erro")).when(calculadorService).recalcular(any(), any());

            assertDoesNotThrow(() -> useCase.execute(List.of(id1)));
            verify(transacaoRepository).saveAll(any());
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar exceção quando lista for nula")
        void deveLancarExcecaoParaListaNula() {
            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(null));

            assertEquals("A lista de IDs não pode ser vazia.", ex.getMessage());
            verify(transacaoRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando lista for vazia")
        void deveLancarExcecaoParaListaVazia() {
            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(List.of()));

            assertEquals("A lista de IDs não pode ser vazia.", ex.getMessage());
            verify(transacaoRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando algum ID não existir — sem salvar nada")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            when(transacaoRepository.findAllByIds(List.of(id1, id2, id3)))
                    .thenReturn(List.of(t1, t2));

            TransacaoNaoEncontradaException ex = assertThrows(TransacaoNaoEncontradaException.class,
                    () -> useCase.execute(List.of(id1, id2, id3)));

            assertEquals("Uma ou mais transações não foram encontradas.", ex.getMessage());
            verify(transacaoRepository, never()).saveAll(any());
            verify(calculadorService, never()).recalcular(any(), any());
        }
    }
}
