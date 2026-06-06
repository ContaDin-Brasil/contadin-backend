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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletarMultiplasTransacoesUseCaseTest {

    @Mock private TransacaoRepository transacaoRepository;
    @Mock private SaldoDiarioCalculadorService calculadorService;

    @InjectMocks
    private DeletarMultiplasTransacoesUseCase useCase;

    private UUID instId;
    private UUID id1, id2, id3;
    private Transacao t1, t2, t3;

    @BeforeEach
    void setup() {
        instId = UUID.randomUUID();
        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        id3 = UUID.randomUUID();

        t1 = transacao(id1, instId, LocalDateTime.of(2026, 5, 1, 0, 0));
        t2 = transacao(id2, instId, LocalDateTime.of(2026, 5, 10, 0, 0));
        t3 = transacao(id3, instId, LocalDateTime.of(2026, 5, 20, 0, 0));
    }

    private Transacao transacao(UUID id, UUID instId, LocalDateTime data) {
        return Transacao.builder()
                .id(id)
                .valor(100.0)
                .tipo(TipoTransacao.GASTO)
                .dataTransacao(data)
                .parcelado(false)
                .ativo(true)
                .fkInstituicao(instId)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve deletar lista de transações e recalcular saldo da instituição")
        void deveDeletarERecalcular() {
            when(transacaoRepository.findAllByIds(List.of(id1, id2, id3)))
                    .thenReturn(List.of(t1, t2, t3));

            useCase.execute(List.of(id1, id2, id3));

            verify(transacaoRepository).deleteAllByIds(List.of(id1, id2, id3));
            // Uma única instituição → um único recálculo a partir da data mais antiga (01/05)
            verify(calculadorService, times(1)).recalcular(instId, t1.getDataTransacao().toLocalDate());
        }

        @Test
        @DisplayName("Deve deletar transação única em lista")
        void deveDeletarListaComUmElemento() {
            when(transacaoRepository.findAllByIds(List.of(id1))).thenReturn(List.of(t1));

            useCase.execute(List.of(id1));

            verify(transacaoRepository).deleteAllByIds(List.of(id1));
            verify(calculadorService).recalcular(instId, t1.getDataTransacao().toLocalDate());
        }

        @Test
        @DisplayName("Deve recalcular uma vez por instituição ao deletar de múltiplas instituições")
        void deveRecalcularUmaVezPorInstituicao() {
            UUID instId2 = UUID.randomUUID();
            Transacao tOutraInst = Transacao.builder()
                    .id(id3)
                    .valor(200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 4, 15, 0, 0))
                    .parcelado(false)
                    .ativo(true)
                    .fkInstituicao(instId2)
                    .build();

            when(transacaoRepository.findAllByIds(List.of(id1, id3)))
                    .thenReturn(List.of(t1, tOutraInst));

            useCase.execute(List.of(id1, id3));

            verify(calculadorService, times(2)).recalcular(any(), any());
            verify(calculadorService).recalcular(instId, t1.getDataTransacao().toLocalDate());
            verify(calculadorService).recalcular(instId2, tOutraInst.getDataTransacao().toLocalDate());
        }

        @Test
        @DisplayName("Deve recalcular a partir da data mais antiga quando há múltiplas da mesma instituição")
        void deveRecalcularDataMaisAntigaDaInstituicao() {
            when(transacaoRepository.findAllByIds(List.of(id1, id2, id3)))
                    .thenReturn(List.of(t1, t2, t3));

            useCase.execute(List.of(id1, id2, id3));

            // t1 tem a data mais antiga (01/05)
            verify(calculadorService).recalcular(instId, t1.getDataTransacao().toLocalDate());
        }

        @Test
        @DisplayName("Deve continuar mesmo quando recálculo falhar")
        void deveContinuarQuandoRecalcularFalhar() {
            when(transacaoRepository.findAllByIds(List.of(id1))).thenReturn(List.of(t1));
            org.mockito.Mockito.doThrow(new RuntimeException("Erro")).when(calculadorService).recalcular(any(), any());

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> useCase.execute(List.of(id1)));
            verify(transacaoRepository).deleteAllByIds(List.of(id1));
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
            verify(transacaoRepository, never()).deleteAllByIds(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando lista for vazia")
        void deveLancarExcecaoParaListaVazia() {
            TransacaoInvalidaException ex = assertThrows(TransacaoInvalidaException.class,
                    () -> useCase.execute(List.of()));

            assertEquals("A lista de IDs não pode ser vazia.", ex.getMessage());
            verify(transacaoRepository, never()).deleteAllByIds(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando algum ID não existir — sem deletar nada")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Pede 3, encontra apenas 2
            when(transacaoRepository.findAllByIds(List.of(id1, id2, id3)))
                    .thenReturn(List.of(t1, t2));

            TransacaoNaoEncontradaException ex = assertThrows(TransacaoNaoEncontradaException.class,
                    () -> useCase.execute(List.of(id1, id2, id3)));

            assertEquals("Uma ou mais transações não foram encontradas.", ex.getMessage());
            verify(transacaoRepository, never()).deleteAllByIds(any());
            verify(calculadorService, never()).recalcular(any(), any());
        }
    }
}
