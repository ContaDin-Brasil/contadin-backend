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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesativarTransacaoUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SaldoDiarioCalculadorService calculadorService;

    @InjectMocks
    private DesativarTransacaoUseCase useCase;

    private UUID transacaoId;
    private UUID fkInstituicao;
    private Transacao transacaoAtiva;

    @BeforeEach
    void setup() {
        transacaoId = UUID.randomUUID();
        fkInstituicao = UUID.randomUUID();

        transacaoAtiva = Transacao.builder()
                .id(transacaoId)
                .valor(250.0)
                .tipo(TipoTransacao.GASTO)
                .descricao("Almoço")
                .dataTransacao(LocalDateTime.of(2026, 5, 15, 12, 0))
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
        @DisplayName("Deve salvar transação com ativo=false")
        void deveDesativarTransacao() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoAtiva));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoId);

            verify(transacaoRepository).save(captor.capture());
            assertFalse(captor.getValue().getAtivo());
        }

        @Test
        @DisplayName("Deve preservar todos os dados da transação ao desativar")
        void devePreservarDadosAoDesativar() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoAtiva));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoId);

            verify(transacaoRepository).save(captor.capture());

            Transacao salva = captor.getValue();
            assertEquals(transacaoAtiva.getId(), salva.getId());
            assertEquals(transacaoAtiva.getValor(), salva.getValor());
            assertEquals(transacaoAtiva.getTipo(), salva.getTipo());
            assertEquals(transacaoAtiva.getDescricao(), salva.getDescricao());
            assertEquals(transacaoAtiva.getDataTransacao(), salva.getDataTransacao());
            assertEquals(transacaoAtiva.getFkInstituicao(), salva.getFkInstituicao());
            assertEquals(transacaoAtiva.getFkCategoria(), salva.getFkCategoria());
            assertFalse(salva.getAtivo());
        }

        @Test
        @DisplayName("Deve manter qtdParcelas quando transação for parcelada")
        void deveManterQtdParcelasQuandoParcelada() {
            Transacao parcelada = Transacao.builder()
                    .id(transacaoId)
                    .valor(1200.0)
                    .tipo(TipoTransacao.GASTO)
                    .dataTransacao(LocalDateTime.of(2026, 5, 15, 12, 0))
                    .parcelado(true)
                    .qtdParcelas(12)
                    .ativo(true)
                    .fkInstituicao(fkInstituicao)
                    .build();

            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(parcelada));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoId);

            verify(transacaoRepository).save(captor.capture());
            assertEquals(12, captor.getValue().getQtdParcelas());
        }

        @Test
        @DisplayName("Deve definir qtdParcelas como null quando transação não for parcelada")
        void deveNullificarQtdParcelasQuandoNaoParcelada() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoAtiva));

            ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);

            useCase.execute(transacaoId);

            verify(transacaoRepository).save(captor.capture());
            assertNull(captor.getValue().getQtdParcelas());
        }

        @Test
        @DisplayName("Deve disparar recálculo do saldo diário na data da transação")
        void deveDispararRecalculo() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoAtiva));

            useCase.execute(transacaoId);

            verify(calculadorService).recalcular(fkInstituicao, LocalDate.of(2026, 5, 15));
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

            assertEquals("ID da transação é obrigatório para desativação.", ex.getMessage());
            verify(transacaoRepository, never()).findById(any());
            verify(transacaoRepository, never()).save(any());
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
            verify(transacaoRepository, never()).save(any());
        }
    }
}
