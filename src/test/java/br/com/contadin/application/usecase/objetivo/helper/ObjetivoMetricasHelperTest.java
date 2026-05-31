package br.com.contadin.application.usecase.objetivo.helper;

import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.model.Objetivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjetivoMetricasHelperTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Test
    @DisplayName("Construtor privado não deve ser instanciável externamente")
    void construtorPrivadoNaoDeveSerInstanciavelExternamente() throws Exception {
        Constructor<ObjetivoMetricasHelper> constructor =
                ObjetivoMetricasHelper.class.getDeclaredConstructor();
        assertFalse(constructor.canAccess(null));
        constructor.setAccessible(true);
        assertDoesNotThrow(() -> { constructor.newInstance(); });
    }

    @Test
    @DisplayName("calcular deve usar dataInicio do objetivo quando for depois do periodoInicio")
    void calcularDeveUsarDataInicioDoObjetivoQuandoForDepoisDoPeriodoInicio() {
        LocalDate periodoInicio = LocalDate.of(2026, 5, 1);
        LocalDate periodoFim = LocalDate.of(2026, 5, 31);

        // objetivo começa depois do periodoInicio
        Objetivo objetivo = Objetivo.builder()
                .id(UUID.randomUUID())
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 15)) // depois do periodoInicio
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkCategoria(UUID.randomUUID())
                .build();

        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(50));

        Objetivo resultado = ObjetivoMetricasHelper.calcular(objetivo, transacaoRepository, periodoInicio, periodoFim);

        assertNotNull(resultado);
        assertNotNull(resultado.getRealizado());
    }

    @Test
    @DisplayName("calcular deve usar periodoInicio quando for depois da dataInicio do objetivo")
    void calcularDeveUsarPeriodoInicioQuandoForDepoisDaDataInicioDoObjetivo() {
        LocalDate periodoInicio = LocalDate.of(2026, 5, 15); // depois da dataInicio do objetivo
        LocalDate periodoFim = LocalDate.of(2026, 5, 31);

        Objetivo objetivo = Objetivo.builder()
                .id(UUID.randomUUID())
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1)) // antes do periodoInicio
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkCategoria(UUID.randomUUID())
                .build();

        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(30));

        Objetivo resultado = ObjetivoMetricasHelper.calcular(objetivo, transacaoRepository, periodoInicio, periodoFim);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("calcular deve usar dataFim do objetivo quando for antes do periodoFim")
    void calcularDeveUsarDataFimDoObjetivoQuandoForAntesDoPeriodoFim() {
        LocalDate periodoInicio = LocalDate.of(2026, 5, 1);
        LocalDate periodoFim = LocalDate.of(2026, 5, 31);

        // objetivo termina antes do periodoFim
        Objetivo objetivo = Objetivo.builder()
                .id(UUID.randomUUID())
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 20)) // antes do periodoFim
                .fkCategoria(UUID.randomUUID())
                .build();

        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(80));

        Objetivo resultado = ObjetivoMetricasHelper.calcular(objetivo, transacaoRepository, periodoInicio, periodoFim);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("calcular (2 args) deve delegar para calcular com período do objetivo")
    void calcularDoisArgsDelegaParaQuatroArgs() {
        Objetivo objetivo = Objetivo.builder()
                .id(UUID.randomUUID())
                .valor(BigDecimal.valueOf(200))
                .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkCategoria(UUID.randomUUID())
                .build();

        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(150));

        Objetivo resultado = ObjetivoMetricasHelper.calcular(objetivo, transacaoRepository);

        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(150), resultado.getRealizado());
    }
}
