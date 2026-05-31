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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletarTransacaoUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SaldoDiarioCalculadorService calculadorService;

    @InjectMocks
    private DeletarTransacaoUseCase useCase;

    private UUID transacaoId;
    private Transacao transacao;

    @BeforeEach
    void setup() {
        transacaoId = UUID.randomUUID();

        transacao = Transacao.builder()
                .id(transacaoId)
                .valor(100.0)
                .tipo(TipoTransacao.GASTO)
                .dataTransacao(LocalDateTime.of(2026, 5, 15, 10, 0))
                .parcelado(false)
                .ativo(true)
                .fkInstituicao(UUID.randomUUID())
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve deletar a transação e disparar recálculo do saldo diário")
        void deveDeletarERecalcular() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacao));

            useCase.execute(transacaoId);

            verify(transacaoRepository).deleteById(transacaoId);
            verify(calculadorService).recalcular(
                    transacao.getFkInstituicao(),
                    LocalDate.of(2026, 5, 15)
            );
        }

        @Test
        @DisplayName("Deve deletar com sucesso mesmo quando recálculo lançar exceção")
        void deveDeletarMesmoQuandoRecalcularFalhar() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacao));
            doThrow(new RuntimeException("Falha no recálculo"))
                    .when(calculadorService).recalcular(any(), any());

            assertDoesNotThrow(() -> useCase.execute(transacaoId));

            verify(transacaoRepository).deleteById(transacaoId);
        }

        @Test
        @DisplayName("Deve verificar existência antes de deletar")
        void deveVerificarExistenciaAntesDeDeletar() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacao));

            useCase.execute(transacaoId);

            verify(transacaoRepository).findById(transacaoId);
            verify(transacaoRepository).deleteById(transacaoId);
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar TransacaoInvalidaException quando ID for nulo")
        void deveLancarExcecaoQuandoIdNulo() {
            TransacaoInvalidaException ex = assertThrows(
                    TransacaoInvalidaException.class,
                    () -> useCase.execute(null)
            );

            assertEquals("ID da transação é obrigatório para exclusão.", ex.getMessage());
            verify(transacaoRepository, never()).findById(any());
            verify(transacaoRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Deve lançar TransacaoNaoEncontradaException quando transação não existir")
        void deveLancarExcecaoQuandoTransacaoNaoExistir() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.empty());

            TransacaoNaoEncontradaException ex = assertThrows(
                    TransacaoNaoEncontradaException.class,
                    () -> useCase.execute(transacaoId)
            );

            assertEquals("Transação não encontrada.", ex.getMessage());
            verify(transacaoRepository, never()).deleteById(any());
        }
    }
}
