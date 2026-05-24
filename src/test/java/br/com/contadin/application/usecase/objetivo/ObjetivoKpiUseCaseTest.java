package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.dto.objetivo.kpi.ImpactoPrevistoKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.MaiorAlertaKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.NoRitmoKpiResponse;
import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Objetivo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjetivoKpiUseCaseTest {

    @Mock
    private ObjetivoRepository objetivoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ObjetivoKpiUseCase useCase;

    private final UUID usuarioId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID catId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID objId = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    void deveLancarQuandoUsuarioNulo() {
        assertThrows(ObjetivoInvalidoException.class,
                () -> useCase.impactoPrevisto(null, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null));
    }

    @Test
    void deveLancarQuandoDataInicioDepoisDeDataFim() {
        assertThrows(ObjetivoInvalidoException.class,
                () -> useCase.noRitmo(usuarioId, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 5, 1), null));
    }

    @Test
    void semObjetivosRetornaZerosEVazio() {
        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of());

        ImpactoPrevistoKpiResponse imp = useCase.impactoPrevisto(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);
        assertEquals(0, imp.impactoPrevistoMes().compareTo(BigDecimal.ZERO));

        NoRitmoKpiResponse nr = useCase.noRitmo(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);
        assertEquals(0, nr.objetivosNoRitmo());
        assertEquals(0, nr.totalObjetivos());
        assertTrue(nr.objetivos().isEmpty());

        MaiorAlertaKpiResponse ma = useCase.maiorAlerta(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);
        assertEquals("--", ma.maiorAlerta());
        assertNull(ma.objetivoId());
    }

    @Test
    void impactoPrevistoSomaEconomiaGastoEReceitaConquistada() {
        Objetivo limite = Objetivo.builder()
                .id(objId)
                .nome("Delivery")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();
        UUID cat2 = UUID.randomUUID();
        Objetivo receita = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("Salário extra")
                .valor(BigDecimal.valueOf(200))
                .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(cat2)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(limite, receita));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catId), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(130));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(cat2), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(50));

        ImpactoPrevistoKpiResponse imp = useCase.impactoPrevisto(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);
        // economia gasto estourado: 2*100-130=70 + receita conquistada 50 = 120
        assertEquals(0, imp.impactoPrevistoMes().compareTo(BigDecimal.valueOf(120)));
    }

    @Test
    void impactoPrevistoCenarioEconomia500MaisReceita400() {
        UUID catGasto = UUID.randomUUID();
        UUID catReceita = UUID.randomUUID();
        Objetivo gasto = Objetivo.builder()
                .valor(BigDecimal.valueOf(600))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catGasto)
                .build();
        Objetivo receita = Objetivo.builder()
                .valor(BigDecimal.valueOf(400))
                .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catReceita)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(gasto, receita));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catGasto), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(100));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catReceita), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(400));

        ImpactoPrevistoKpiResponse imp = useCase.impactoPrevisto(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);
        assertEquals(0, imp.impactoPrevistoMes().compareTo(BigDecimal.valueOf(900)));
    }

    @Test
    void noRitmoRetornaListaDeObjetivosNoCaminho() {
        UUID catTranquila = UUID.randomUUID();
        Objetivo tranquilo = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("Delivery")
                .valor(BigDecimal.valueOf(600))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catTranquila)
                .build();
        Objetivo estourado = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("Supermercado")
                .valor(BigDecimal.valueOf(500))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(tranquilo, estourado));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catTranquila), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(100));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catId), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(620));

        NoRitmoKpiResponse nr = useCase.noRitmo(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        assertEquals(1, nr.objetivosNoRitmo());
        assertEquals(2, nr.totalObjetivos());
        assertEquals(1, nr.objetivos().size());
        assertEquals("Delivery", nr.objetivos().get(0).nome());
        assertEquals(StatusObjetivo.TRANQUILO, nr.objetivos().get(0).status());
    }

    @Test
    void maiorAlertaEscolhePiorScoreEGeraTexto() {
        UUID catTranquila = UUID.randomUUID();
        Objetivo tranquilo = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("A")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catTranquila)
                .build();
        Objetivo cuidado = Objetivo.builder()
                .id(objId)
                .nome("B")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(tranquilo, cuidado));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catTranquila), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(40));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catId), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(80));

        when(categoriaRepository.findById(catId)).thenReturn(Optional.of(
                Categoria.builder().id(catId).nome("Salario").build()));

        MaiorAlertaKpiResponse ma = useCase.maiorAlerta(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);
        assertEquals(objId, ma.objetivoId());
        assertEquals("Salario: 80% do limite usado", ma.maiorAlerta());
    }

    @Test
    void deveFiltrarObjetivosPorTipo() {
        Objetivo limiteGasto = Objetivo.builder()
                .id(objId)
                .nome("Limite")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();
        UUID cat2 = UUID.randomUUID();
        Objetivo aumentoReceita = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("Meta")
                .valor(BigDecimal.valueOf(500))
                .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(cat2)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(limiteGasto, aumentoReceita));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        // Filtra apenas LIMITE_GASTO → só 1 objetivo
        NoRitmoKpiResponse nr = useCase.noRitmo(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), TipoObjetivo.LIMITE_GASTO);

        assertEquals(1, nr.totalObjetivos());
    }

    @Test
    void deveUsarPeriodoPadraoQuandoDatasNulas() {
        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of());

        // Não deve lançar exceção quando datas são null
        assertDoesNotThrow(() -> useCase.impactoPrevisto(usuarioId, null, null, null));
        assertDoesNotThrow(() -> useCase.noRitmo(usuarioId, null, null, null));
        assertDoesNotThrow(() -> useCase.maiorAlerta(usuarioId, null, null, null));
    }

    @Test
    void deveExcluirObjetivoForaDoPeriodoDaConsulta() {
        // Objetivo com período anterior ao período de consulta
        Objetivo antigo = Objetivo.builder()
                .id(objId)
                .nome("Antigo")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2025, 1, 1))
                .dataFim(LocalDate.of(2025, 3, 31)) // termina antes do período de consulta
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(antigo));

        // Consulta em mai/2026 - objetivo de jan-mar/2025 não deve aparecer
        NoRitmoKpiResponse nr = useCase.noRitmo(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        assertEquals(0, nr.totalObjetivos());
    }

    @Test
    void impactoPrevistoComRealizadoMenorQueValor() {
        // realizado <= valor → economia = valor - realizado (sem dobrar)
        Objetivo limite = Objetivo.builder()
                .id(objId)
                .nome("Delivery")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(limite));
        // realizado = 40, valor = 100 → economia = 100-40 = 60
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catId), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(40));

        ImpactoPrevistoKpiResponse imp = useCase.impactoPrevisto(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        assertEquals(0, imp.impactoPrevistoMes().compareTo(BigDecimal.valueOf(60)));
    }

    @Test
    void impactoPrevistoEconomiaNegativaRetornaZero() {
        // realizado muito alto → 2*valor - realizado < 0 → max(0) = 0
        Objetivo limite = Objetivo.builder()
                .id(objId)
                .nome("Delivery")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(limite));
        // realizado = 250, valor = 100 → 2*100-250 = -50 → max(0) = 0
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catId), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(250));

        ImpactoPrevistoKpiResponse imp = useCase.impactoPrevisto(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        assertEquals(0, imp.impactoPrevistoMes().compareTo(BigDecimal.ZERO));
    }

    @Test
    void noRitmoParaObjetivoAumentoReceitaNaoConcluido() {
        // AUMENTO_RECEITA: "no ritmo" = status != NO_CAMINHO (i.e., está alcançando a meta)
        // realizado = 500 >= valor = 500 → 100% → status CONCLUIDO ou TRANQUILO para AUMENTO_RECEITA
        UUID catReceita = UUID.randomUUID();
        Objetivo receita = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("Meta Receita")
                .valor(BigDecimal.valueOf(200))
                .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catReceita)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(receita));
        // realizado = 200 = valor → 100% → deve ser NO_CAMINHO ou CONCLUIDO para AUMENTO_RECEITA
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catReceita), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(200));

        NoRitmoKpiResponse nr = useCase.noRitmo(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        // totalObjetivos = 1 (foi processado)
        assertEquals(1, nr.totalObjetivos());
    }

    @Test
    void maiorAlertaParaObjetivoAumentoReceita() {
        UUID catReceita = UUID.randomUUID();
        Objetivo receita = Objetivo.builder()
                .id(objId)
                .nome("Meta Receita")
                .valor(BigDecimal.valueOf(500))
                .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catReceita)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(receita));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catReceita), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(250));

        when(categoriaRepository.findById(catReceita)).thenReturn(Optional.of(
                Categoria.builder().id(catReceita).nome("Salários").build()));

        MaiorAlertaKpiResponse ma = useCase.maiorAlerta(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        assertNotNull(ma.maiorAlerta());
        assertTrue(ma.maiorAlerta().contains("% da meta de receita"));
        assertEquals(objId, ma.objetivoId());
    }

    @Test
    void maiorAlertaFallbackParaNomeQuandoCategoriaNaoEncontrada() {
        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(
                Objetivo.builder()
                        .id(objId)
                        .nome("Meu Objetivo")
                        .valor(BigDecimal.valueOf(100))
                        .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                        .dataInicio(LocalDate.of(2026, 5, 1))
                        .dataFim(LocalDate.of(2026, 5, 31))
                        .fkUsuario(usuarioId)
                        .fkCategoria(catId)
                        .build()
        ));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catId), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(80));

        // Categoria não encontrada → usa nome do objetivo
        when(categoriaRepository.findById(catId)).thenReturn(Optional.empty());

        MaiorAlertaKpiResponse ma = useCase.maiorAlerta(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        assertTrue(ma.maiorAlerta().startsWith("Meu Objetivo:"));
    }

    @Test
    void gapMonetarioZeroQuandoLimiteGastoSemExcesso() {
        // LIMITE_GASTO onde realizado < valor → excedente < 0 → yield 0
        // Isso afeta o ordenamento por gapMonetario (tiebreaker) sem impactar o resultado
        Objetivo a = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("A")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catId)
                .build();
        UUID catB = UUID.randomUUID();
        Objetivo b = Objetivo.builder()
                .id(objId)
                .nome("B")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catB)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(a, b));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catId), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(50)); // realizado < valor → sem excesso
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catB), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(80)); // maior percentual

        when(categoriaRepository.findById(catB)).thenReturn(Optional.of(
                Categoria.builder().id(catB).nome("Cat B").build()));

        MaiorAlertaKpiResponse ma = useCase.maiorAlerta(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null);

        // B tem maior percentual (80/100=80% vs 50/100=50%) → B é o maior alerta
        assertEquals(objId, ma.objetivoId());
    }

    @Test
    void gapMonetarioZeroQuandoAumentoReceitaSemDeficit() {
        // AUMENTO_RECEITA onde realizado >= valor → falta <= 0 → yield 0
        UUID catReceita = UUID.randomUUID();
        Objetivo a = Objetivo.builder()
                .id(UUID.randomUUID())
                .nome("A")
                .valor(BigDecimal.valueOf(100))
                .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                .dataInicio(LocalDate.of(2026, 5, 1))
                .dataFim(LocalDate.of(2026, 5, 31))
                .fkUsuario(usuarioId)
                .fkCategoria(catReceita)
                .build();

        when(objetivoRepository.findByUsuario(usuarioId)).thenReturn(List.of(a));
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(eq(catReceita), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(120)); // realizado > valor → sem déficit

        when(categoriaRepository.findById(catReceita)).thenReturn(Optional.of(
                Categoria.builder().id(catReceita).nome("Receita A").build()));

        // Não deve lançar exceção
        assertDoesNotThrow(() -> useCase.maiorAlerta(usuarioId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), null));
    }
}
