package br.com.contadin.application.usecase.saldo;

import br.com.contadin.application.dto.saldo.SaldoNaDataResponse;
import br.com.contadin.application.dto.saldo.SaldoPorInstituicaoResponse;
import br.com.contadin.application.port.out.SaldoDiarioRepository;
import br.com.contadin.domain.model.SaldoDiario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarSaldoDiarioUseCaseTest {

    @Mock
    private SaldoDiarioRepository saldoDiarioRepository;

    @InjectMocks
    private BuscarSaldoDiarioUseCase useCase;

    private UUID fkUsuario;
    private UUID fkInstituicao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private SaldoDiario saldoDiario;

    @BeforeEach
    void setup() {
        fkUsuario = UUID.randomUUID();
        fkInstituicao = UUID.randomUUID();
        dataInicio = LocalDate.of(2026, 5, 1);
        dataFim = LocalDate.of(2026, 5, 31);

        saldoDiario = SaldoDiario.builder()
                .id(UUID.randomUUID())
                .fkInstituicao(fkInstituicao)
                .fkUsuario(fkUsuario)
                .data(LocalDate.of(2026, 5, 15))
                .saldoInicial(new BigDecimal("1000.00"))
                .totalReceitas(new BigDecimal("500.00"))
                .totalGastos(new BigDecimal("200.00"))
                .saldoFinal(new BigDecimal("1300.00"))
                .build();
    }

    // -----------------------------------------------------------------------
    // buscarPorInstituicao
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("buscarPorInstituicao — busca por período de uma instituição")
    class BuscarPorInstituicao {

        @Test
        @DisplayName("Deve retornar lista de saldos do período delegando ao repositório")
        void deveRetornarListaDeSaldosDoPeriodo() {
            List<SaldoDiario> esperado = List.of(saldoDiario);

            when(saldoDiarioRepository.buscarPorInstituicaoEPeriodo(fkInstituicao, dataInicio, dataFim))
                    .thenReturn(esperado);

            List<SaldoDiario> resultado = useCase.buscarPorInstituicao(fkInstituicao, dataInicio, dataFim);

            assertEquals(esperado, resultado);
            verify(saldoDiarioRepository).buscarPorInstituicaoEPeriodo(fkInstituicao, dataInicio, dataFim);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver saldos no período")
        void deveRetornarListaVaziaQuandoSemSaldos() {
            when(saldoDiarioRepository.buscarPorInstituicaoEPeriodo(fkInstituicao, dataInicio, dataFim))
                    .thenReturn(List.of());

            List<SaldoDiario> resultado = useCase.buscarPorInstituicao(fkInstituicao, dataInicio, dataFim);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // -----------------------------------------------------------------------
    // buscarConsolidado
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("buscarConsolidado — saldo consolidado de todas as instituições do usuário")
    class BuscarConsolidado {

        @Test
        @DisplayName("Deve retornar lista consolidada delegando ao repositório")
        void deveRetornarListaConsolidada() {
            List<SaldoDiario> esperado = List.of(saldoDiario);

            when(saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, dataInicio, dataFim))
                    .thenReturn(esperado);

            List<SaldoDiario> resultado = useCase.buscarConsolidado(fkUsuario, dataInicio, dataFim);

            assertEquals(esperado, resultado);
            verify(saldoDiarioRepository).buscarConsolidadoPorUsuario(fkUsuario, dataInicio, dataFim);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver saldo consolidado")
        void deveRetornarListaVaziaQuandoSemConsolidado() {
            when(saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, dataInicio, dataFim))
                    .thenReturn(List.of());

            List<SaldoDiario> resultado = useCase.buscarConsolidado(fkUsuario, dataInicio, dataFim);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // -----------------------------------------------------------------------
    // buscarSaldoNaData
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("buscarSaldoNaData — saldo consolidado do usuário em uma data específica")
    class BuscarSaldoNaData {

        @Test
        @DisplayName("Deve retornar SaldoNaDataResponse com dados do SaldoDiario quando existir resultado")
        void deveRetornarSaldoNaDataQuandoExistirResultado() {
            LocalDate data = LocalDate.of(2026, 5, 15);

            when(saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, data, data))
                    .thenReturn(List.of(saldoDiario));

            SaldoNaDataResponse response = useCase.buscarSaldoNaData(fkUsuario, data);

            assertNotNull(response);
            assertEquals(saldoDiario.getData(), response.data());
            assertEquals(saldoDiario.getSaldoFinal(), response.saldoTotal());
            assertEquals(saldoDiario.getTotalReceitas(), response.totalReceitas());
            assertEquals(saldoDiario.getTotalGastos(), response.totalGastos());
        }

        @Test
        @DisplayName("Deve retornar zeros quando não houver saldo na data")
        void deveRetornarZerosQuandoNaoHouverSaldoNaData() {
            LocalDate data = LocalDate.of(2026, 5, 15);

            when(saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, data, data))
                    .thenReturn(List.of());

            SaldoNaDataResponse response = useCase.buscarSaldoNaData(fkUsuario, data);

            assertNotNull(response);
            assertEquals(data, response.data());
            assertEquals(BigDecimal.ZERO, response.saldoTotal());
            assertEquals(BigDecimal.ZERO, response.totalReceitas());
            assertEquals(BigDecimal.ZERO, response.totalGastos());
        }

        @Test
        @DisplayName("Deve usar a mesma data como início e fim da consulta ao repositório")
        void deveUsarMesmaDataParaInicioEFim() {
            LocalDate data = LocalDate.of(2026, 5, 20);

            when(saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, data, data))
                    .thenReturn(List.of(saldoDiario));

            useCase.buscarSaldoNaData(fkUsuario, data);

            verify(saldoDiarioRepository).buscarConsolidadoPorUsuario(fkUsuario, data, data);
        }

        @Test
        @DisplayName("Deve usar apenas o primeiro resultado quando houver múltiplos saldos")
        void deveUsarApenasPrimeiroResultado() {
            LocalDate data = LocalDate.of(2026, 5, 15);

            SaldoDiario saldoSecundario = SaldoDiario.builder()
                    .data(data)
                    .saldoFinal(new BigDecimal("9999.00"))
                    .totalReceitas(new BigDecimal("9000.00"))
                    .totalGastos(new BigDecimal("100.00"))
                    .build();

            when(saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, data, data))
                    .thenReturn(List.of(saldoDiario, saldoSecundario));

            SaldoNaDataResponse response = useCase.buscarSaldoNaData(fkUsuario, data);

            assertEquals(saldoDiario.getSaldoFinal(), response.saldoTotal());
        }
    }

    // -----------------------------------------------------------------------
    // buscarSaldoPorInstituicoesNaData
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("buscarSaldoPorInstituicoesNaData — saldo por instituição em uma data")
    class BuscarSaldoPorInstituicoesNaData {

        @Test
        @DisplayName("Deve buscar por instituição específica quando fkInstituicao for informado")
        void deveBuscarPorInstituicaoEspecifica() {
            LocalDate data = LocalDate.of(2026, 5, 15);

            when(saldoDiarioRepository.buscarPorInstituicaoNaData(fkInstituicao, data))
                    .thenReturn(List.of(saldoDiario));

            List<SaldoPorInstituicaoResponse> resultado =
                    useCase.buscarSaldoPorInstituicoesNaData(fkUsuario, fkInstituicao, data);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());

            verify(saldoDiarioRepository).buscarPorInstituicaoNaData(fkInstituicao, data);
            verify(saldoDiarioRepository, never()).buscarPorUsuarioNaData(fkUsuario, data);
        }

        @Test
        @DisplayName("Deve buscar todas as instituições do usuário quando fkInstituicao for nulo")
        void deveBuscarTodasInstituicoesQuandoInstituicaoNula() {
            LocalDate data = LocalDate.of(2026, 5, 15);

            when(saldoDiarioRepository.buscarPorUsuarioNaData(fkUsuario, data))
                    .thenReturn(List.of(saldoDiario));

            List<SaldoPorInstituicaoResponse> resultado =
                    useCase.buscarSaldoPorInstituicoesNaData(fkUsuario, null, data);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());

            verify(saldoDiarioRepository).buscarPorUsuarioNaData(fkUsuario, data);
            verify(saldoDiarioRepository, never()).buscarPorInstituicaoNaData(fkInstituicao, data);
        }

        @Test
        @DisplayName("Deve mapear corretamente SaldoDiario para SaldoPorInstituicaoResponse")
        void deveMapearCorretamenteSaldoDiarioParaResponse() {
            LocalDate data = LocalDate.of(2026, 5, 15);

            when(saldoDiarioRepository.buscarPorInstituicaoNaData(fkInstituicao, data))
                    .thenReturn(List.of(saldoDiario));

            List<SaldoPorInstituicaoResponse> resultado =
                    useCase.buscarSaldoPorInstituicoesNaData(fkUsuario, fkInstituicao, data);

            SaldoPorInstituicaoResponse item = resultado.get(0);
            assertEquals(saldoDiario.getFkInstituicao(), item.fkInstituicao());
            assertEquals(saldoDiario.getData(), item.data());
            assertEquals(saldoDiario.getSaldoFinal(), item.saldoFinal());
            assertEquals(saldoDiario.getTotalReceitas(), item.totalReceitas());
            assertEquals(saldoDiario.getTotalGastos(), item.totalGastos());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver saldos na data")
        void deveRetornarListaVaziaQuandoSemSaldosNaData() {
            LocalDate data = LocalDate.of(2026, 5, 15);

            when(saldoDiarioRepository.buscarPorInstituicaoNaData(fkInstituicao, data))
                    .thenReturn(List.of());

            List<SaldoPorInstituicaoResponse> resultado =
                    useCase.buscarSaldoPorInstituicoesNaData(fkUsuario, fkInstituicao, data);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve mapear lista com múltiplos SaldoDiario para lista de responses")
        void deveMapearMultiplosSaldos() {
            LocalDate data = LocalDate.of(2026, 5, 15);
            UUID outraInstituicao = UUID.randomUUID();

            SaldoDiario saldo2 = SaldoDiario.builder()
                    .fkInstituicao(outraInstituicao)
                    .fkUsuario(fkUsuario)
                    .data(data)
                    .saldoFinal(new BigDecimal("800.00"))
                    .totalReceitas(new BigDecimal("1000.00"))
                    .totalGastos(new BigDecimal("200.00"))
                    .build();

            when(saldoDiarioRepository.buscarPorUsuarioNaData(fkUsuario, data))
                    .thenReturn(List.of(saldoDiario, saldo2));

            List<SaldoPorInstituicaoResponse> resultado =
                    useCase.buscarSaldoPorInstituicoesNaData(fkUsuario, null, data);

            assertEquals(2, resultado.size());
            assertEquals(saldoDiario.getFkInstituicao(), resultado.get(0).fkInstituicao());
            assertEquals(outraInstituicao, resultado.get(1).fkInstituicao());
        }
    }
}
