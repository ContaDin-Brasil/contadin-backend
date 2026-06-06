package br.com.contadin.application.service;

import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.SaldoDiarioRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.domain.model.SaldoDiario;
import br.com.contadin.domain.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaldoDiarioCalculadorServiceTest {

    @Mock private SaldoDiarioRepository saldoDiarioRepository;
    @Mock private InstituicaoRepository instituicaoRepository;
    @Mock private TransacaoRepository transacaoRepository;

    @InjectMocks
    private SaldoDiarioCalculadorService service;

    private UUID instId;
    private UUID usuarioId;
    private Instituicao instituicao;

    @BeforeEach
    void setUp() {
        instId   = UUID.randomUUID();
        usuarioId = UUID.randomUUID();

        instituicao = Instituicao.builder()
                .id(instId)
                .fkUsuario(usuarioId)
                .saldoInicial(BigDecimal.ZERO)
                .build();

        lenient().when(instituicaoRepository.findById(instId)).thenReturn(Optional.of(instituicao));
        lenient().when(saldoDiarioRepository.buscarUltimoAntesDeData(any(), any())).thenReturn(Optional.empty());
        lenient().doNothing().when(saldoDiarioRepository).deletarAPartirDeData(any(), any());
        lenient().when(saldoDiarioRepository.salvarTodos(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    // ──────────────────────────────────────────────
    // helpers
    // ──────────────────────────────────────────────

    private List<SaldoDiario> recalcular(LocalDate dataMinima, List<Transacao> transacoes) {
        when(transacaoRepository.findAllAtivasByInstituicao(instId)).thenReturn(transacoes);

        service.recalcular(instId, dataMinima);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SaldoDiario>> captor = ArgumentCaptor.forClass(List.class);
        verify(saldoDiarioRepository).salvarTodos(captor.capture());
        return captor.getValue();
    }

    private SaldoDiario saldoEm(List<SaldoDiario> snapshots, LocalDate data) {
        return snapshots.stream()
                .filter(s -> s.getData().isEqual(data))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Snapshot não encontrado para " + data));
    }

    private Transacao transacaoPontual(TipoTransacao tipo, double valor, LocalDate data) {
        return Transacao.builder()
                .tipo(tipo)
                .valor(valor)
                .dataTransacao(data.atStartOfDay())
                .parcelado(false)
                .fkInstituicao(instId)
                .ativo(true)
                .build();
    }

    private Transacao transacaoParcelada(TipoTransacao tipo, double valor, LocalDate primeiraParcela, int qtdParcelas) {
        return Transacao.builder()
                .tipo(tipo)
                .valor(valor)
                .dataTransacao(primeiraParcela.atStartOfDay())
                .parcelado(true)
                .qtdParcelas(qtdParcelas)
                .fkInstituicao(instId)
                .ativo(true)
                .build();
    }

    private Transacao transacaoRecorrente(TipoTransacao tipo, double valor, LocalDate inicio,
                                          Recorrencia recorrencia, LocalDate fim) {
        Date fimDate = fim != null
                ? Date.from(fim.atStartOfDay(ZoneId.systemDefault()).toInstant())
                : null;
        return Transacao.builder()
                .tipo(tipo)
                .valor(valor)
                .dataTransacao(inicio.atStartOfDay())
                .parcelado(false)
                .recorrencia(recorrencia)
                .fimRecorrencia(fimDate)
                .fkInstituicao(instId)
                .ativo(true)
                .build();
    }

    // ──────────────────────────────────────────────
    // Testes
    // ──────────────────────────────────────────────

    @Test
    void umaReceitaPontual_propagaSaldoParaFrente() {
        LocalDate hoje = LocalDate.now();
        var transacoes = List.of(transacaoPontual(TipoTransacao.RECEITA, 500.0, hoje));

        List<SaldoDiario> snapshots = recalcular(hoje, transacoes);

        SaldoDiario diaReceita = saldoEm(snapshots, hoje);
        assertThat(diaReceita.getSaldoInicial()).isEqualByComparingTo("0.00");
        assertThat(diaReceita.getTotalReceitas()).isEqualByComparingTo("500.00");
        assertThat(diaReceita.getTotalGastos()).isEqualByComparingTo("0.00");
        assertThat(diaReceita.getSaldoFinal()).isEqualByComparingTo("500.00");

        // dia seguinte deve iniciar com 500
        SaldoDiario diaSeguinte = saldoEm(snapshots, hoje.plusDays(1));
        assertThat(diaSeguinte.getSaldoInicial()).isEqualByComparingTo("500.00");
        assertThat(diaSeguinte.getTotalReceitas()).isEqualByComparingTo("0.00");
        assertThat(diaSeguinte.getSaldoFinal()).isEqualByComparingTo("500.00");
    }

    @Test
    void receitaEGastoNoDiaSeguinte_calculaSaldosCascata() {
        LocalDate d0 = LocalDate.now();
        LocalDate d1 = d0.plusDays(1);

        var transacoes = List.of(
                transacaoPontual(TipoTransacao.RECEITA, 1000.0, d0),
                transacaoPontual(TipoTransacao.GASTO,   300.0,  d1)
        );

        List<SaldoDiario> snapshots = recalcular(d0, transacoes);

        SaldoDiario sd0 = saldoEm(snapshots, d0);
        assertThat(sd0.getSaldoFinal()).isEqualByComparingTo("1000.00");

        SaldoDiario sd1 = saldoEm(snapshots, d1);
        assertThat(sd1.getSaldoInicial()).isEqualByComparingTo("1000.00");
        assertThat(sd1.getTotalGastos()).isEqualByComparingTo("300.00");
        assertThat(sd1.getSaldoFinal()).isEqualByComparingTo("700.00");

        // dois dias depois saldo deve continuar 700
        SaldoDiario sd2 = saldoEm(snapshots, d1.plusDays(1));
        assertThat(sd2.getSaldoFinal()).isEqualByComparingTo("700.00");
    }

    @Test
    void multiplosGastosEReceitasNoMesmoDia_somaCorretamente() {
        LocalDate hoje = LocalDate.now();

        var transacoes = List.of(
                transacaoPontual(TipoTransacao.RECEITA, 2000.0, hoje),
                transacaoPontual(TipoTransacao.RECEITA,  500.0, hoje),
                transacaoPontual(TipoTransacao.GASTO,    400.0, hoje),
                transacaoPontual(TipoTransacao.GASTO,    100.0, hoje)
        );

        List<SaldoDiario> snapshots = recalcular(hoje, transacoes);

        SaldoDiario sd = saldoEm(snapshots, hoje);
        assertThat(sd.getTotalReceitas()).isEqualByComparingTo("2500.00");
        assertThat(sd.getTotalGastos()).isEqualByComparingTo("500.00");
        assertThat(sd.getSaldoFinal()).isEqualByComparingTo("2000.00");
    }

    @Test
    void transacaoRecorrenteMensal_ocorreTodoMes() {
        // Salário dia 5 de cada mês, começa este mês
        LocalDate inicio = LocalDate.now().withDayOfMonth(5);
        if (inicio.isBefore(LocalDate.now())) {
            inicio = inicio.plusMonths(1); // próximo mês se já passou
        }
        LocalDate fimRecorrencia = inicio.plusMonths(3);

        var transacoes = List.of(
                transacaoRecorrente(TipoTransacao.RECEITA, 3000.0, inicio, Recorrencia.MENSAL, fimRecorrencia)
        );

        List<SaldoDiario> snapshots = recalcular(inicio, transacoes);

        // deve ocorrer no dia de início
        SaldoDiario sdInicio = saldoEm(snapshots, inicio);
        assertThat(sdInicio.getTotalReceitas()).isEqualByComparingTo("3000.00");

        // deve ocorrer um mês depois
        SaldoDiario sdMes1 = saldoEm(snapshots, inicio.plusMonths(1));
        assertThat(sdMes1.getTotalReceitas()).isEqualByComparingTo("3000.00");

        // deve ocorrer dois meses depois
        SaldoDiario sdMes2 = saldoEm(snapshots, inicio.plusMonths(2));
        assertThat(sdMes2.getTotalReceitas()).isEqualByComparingTo("3000.00");

        // após fimRecorrencia não deve ocorrer
        SaldoDiario sdAposFim = saldoEm(snapshots, fimRecorrencia.plusMonths(1));
        assertThat(sdAposFim.getTotalReceitas()).isEqualByComparingTo("0.00");
    }

    @Test
    void transacaoRecorrenteSemanal_ocorreCada7Dias() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = inicio.plusWeeks(3);

        var transacoes = List.of(
                transacaoRecorrente(TipoTransacao.GASTO, 150.0, inicio, Recorrencia.SEMANAL, fim)
        );

        List<SaldoDiario> snapshots = recalcular(inicio, transacoes);

        assertThat(saldoEm(snapshots, inicio).getTotalGastos()).isEqualByComparingTo("150.00");
        assertThat(saldoEm(snapshots, inicio.plusWeeks(1)).getTotalGastos()).isEqualByComparingTo("150.00");
        assertThat(saldoEm(snapshots, inicio.plusWeeks(2)).getTotalGastos()).isEqualByComparingTo("150.00");
        assertThat(saldoEm(snapshots, inicio.plusWeeks(3)).getTotalGastos()).isEqualByComparingTo("150.00");

        // entre semanas: sem gasto
        assertThat(saldoEm(snapshots, inicio.plusDays(1)).getTotalGastos()).isEqualByComparingTo("0.00");
        assertThat(saldoEm(snapshots, inicio.plusDays(3)).getTotalGastos()).isEqualByComparingTo("0.00");
    }

    @Test
    void transacaoRecorrenteDiaria_ocorreTodosDosOsDias() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = inicio.plusDays(4);

        var transacoes = List.of(
                transacaoRecorrente(TipoTransacao.GASTO, 10.0, inicio, Recorrencia.DIARIO, fim)
        );

        List<SaldoDiario> snapshots = recalcular(inicio, transacoes);

        for (int i = 0; i <= 4; i++) {
            assertThat(saldoEm(snapshots, inicio.plusDays(i)).getTotalGastos())
                    .as("Dia +" + i)
                    .isEqualByComparingTo("10.00");
        }

        // após o fim: sem gasto
        assertThat(saldoEm(snapshots, inicio.plusDays(5)).getTotalGastos()).isEqualByComparingTo("0.00");
    }

    @Test
    void transacaoRecorrenteAnual_ocorreNoMesmoMesDia() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = inicio.plusYears(2);

        var transacoes = List.of(
                transacaoRecorrente(TipoTransacao.RECEITA, 500.0, inicio, Recorrencia.ANUAL, fim)
        );

        List<SaldoDiario> snapshots = recalcular(inicio, transacoes);

        assertThat(saldoEm(snapshots, inicio).getTotalReceitas()).isEqualByComparingTo("500.00");

        // dia seguinte: sem receita
        assertThat(saldoEm(snapshots, inicio.plusDays(1)).getTotalReceitas()).isEqualByComparingTo("0.00");

        // um mês depois: sem receita
        assertThat(saldoEm(snapshots, inicio.plusMonths(1)).getTotalReceitas()).isEqualByComparingTo("0.00");
    }

    @Test
    void transacaoParcelada_distribuiValorEmNMeses() {
        LocalDate primeiraParcela = LocalDate.now();
        List<SaldoDiario> snapshots = recalcular(primeiraParcela,
                List.of(transacaoParcelada(TipoTransacao.GASTO, 600.0, primeiraParcela, 3)));

        // Cada parcela = 600 / 3 = 200
        assertThat(saldoEm(snapshots, primeiraParcela).getTotalGastos()).isEqualByComparingTo("200.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(1)).getTotalGastos()).isEqualByComparingTo("200.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(2)).getTotalGastos()).isEqualByComparingTo("200.00");
    }

    @Test
    void transacaoParcelada_diasIntermediarios_semImpacto() {
        LocalDate primeiraParcela = LocalDate.now();
        List<SaldoDiario> snapshots = recalcular(primeiraParcela,
                List.of(transacaoParcelada(TipoTransacao.GASTO, 300.0, primeiraParcela, 3)));

        assertThat(saldoEm(snapshots, primeiraParcela.plusDays(1)).getTotalGastos()).isEqualByComparingTo("0.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusDays(15)).getTotalGastos()).isEqualByComparingTo("0.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(1).plusDays(1)).getTotalGastos()).isEqualByComparingTo("0.00");
    }

    @Test
    void transacaoParcelada_parcelasAlem6Meses_naoAparecem() {
        LocalDate hoje = LocalDate.now();
        // 10 parcelas: meses 0-6 ficam dentro da janela, meses 7-9 ficam fora
        List<SaldoDiario> snapshots = recalcular(hoje,
                List.of(transacaoParcelada(TipoTransacao.GASTO, 1000.0, hoje, 10)));

        // Parcela do mês 6 (último dentro da janela): deve aparecer
        assertThat(saldoEm(snapshots, hoje.plusMonths(6)).getTotalGastos()).isEqualByComparingTo("100.00");

        // Mês 7 está além da janela: não há snapshot para essa data
        LocalDate alem = hoje.plusMonths(7);
        assertThat(snapshots.stream().anyMatch(s -> s.getData().isEqual(alem))).isFalse();
    }

    @Test
    void transacaoParcelada_valorNaoDivisivel_arredondaHalfUp() {
        LocalDate primeiraParcela = LocalDate.now();
        // 100 / 3 = 33.3333... → arredonda para 33.33 (HALF_UP)
        List<SaldoDiario> snapshots = recalcular(primeiraParcela,
                List.of(transacaoParcelada(TipoTransacao.GASTO, 100.0, primeiraParcela, 3)));

        assertThat(saldoEm(snapshots, primeiraParcela).getTotalGastos()).isEqualByComparingTo("33.33");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(1)).getTotalGastos()).isEqualByComparingTo("33.33");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(2)).getTotalGastos()).isEqualByComparingTo("33.33");
    }

    @Test
    void transacaoParcelada_diaInexistenteNoMesSeguinte_ajustaUltimoDia() {
        // Jan 31 + 1 mês → Java ajusta para Fev 28 (2026 não é bissexto)
        LocalDate jan31 = LocalDate.of(2026, 1, 31);
        List<SaldoDiario> snapshots = recalcular(jan31,
                List.of(transacaoParcelada(TipoTransacao.GASTO, 300.0, jan31, 3)));

        assertThat(saldoEm(snapshots, LocalDate.of(2026, 1, 31)).getTotalGastos()).isEqualByComparingTo("100.00");
        assertThat(saldoEm(snapshots, LocalDate.of(2026, 2, 28)).getTotalGastos()).isEqualByComparingTo("100.00");
        assertThat(saldoEm(snapshots, LocalDate.of(2026, 3, 31)).getTotalGastos()).isEqualByComparingTo("100.00");
    }

    @Test
    void transacaoParcelada_comSaldoSemente_propagaCorretamente() {
        LocalDate primeiraParcela = LocalDate.now();
        when(saldoDiarioRepository.buscarUltimoAntesDeData(instId, primeiraParcela))
                .thenReturn(Optional.of(SaldoDiario.builder().saldoFinal(new BigDecimal("2000.00")).build()));

        // R$1500 em 3x → R$500/mês
        List<SaldoDiario> snapshots = recalcular(primeiraParcela,
                List.of(transacaoParcelada(TipoTransacao.GASTO, 1500.0, primeiraParcela, 3)));

        assertThat(saldoEm(snapshots, primeiraParcela).getSaldoFinal()).isEqualByComparingTo("1500.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(1)).getSaldoFinal()).isEqualByComparingTo("1000.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(2)).getSaldoFinal()).isEqualByComparingTo("500.00");
    }

    @Test
    void doisParcelados_mesmaDataInicio_somamGastosCorretamente() {
        LocalDate primeiraParcela = LocalDate.now();
        List<SaldoDiario> snapshots = recalcular(primeiraParcela, List.of(
                transacaoParcelada(TipoTransacao.GASTO, 300.0, primeiraParcela, 3),  // 100/mês
                transacaoParcelada(TipoTransacao.GASTO, 600.0, primeiraParcela, 3)   // 200/mês
        ));

        // Ambos ocorrem no mesmo dia → R$300 total por mês
        assertThat(saldoEm(snapshots, primeiraParcela).getTotalGastos()).isEqualByComparingTo("300.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(1)).getTotalGastos()).isEqualByComparingTo("300.00");
        assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(2)).getTotalGastos()).isEqualByComparingTo("300.00");
    }

    @Test
    void transacaoParcelada_receita_distribuiMensalmente() {
        LocalDate primeiraParcela = LocalDate.now();
        // Recebimento de freelance em 6x R$1000
        List<SaldoDiario> snapshots = recalcular(primeiraParcela,
                List.of(transacaoParcelada(TipoTransacao.RECEITA, 6000.0, primeiraParcela, 6)));

        for (int i = 0; i < 6; i++) {
            assertThat(saldoEm(snapshots, primeiraParcela.plusMonths(i)).getTotalReceitas())
                    .as("Mês +" + i)
                    .isEqualByComparingTo("1000.00");
        }
    }

    @Test
    void efeitoCascata_transacaoAnteriorImpactaTodosOsDiasSeguintes() {
        // Receita de 1000 em d0, gasto de 200 em d2
        LocalDate d0 = LocalDate.now();
        LocalDate d2 = d0.plusDays(2);

        var transacoes = List.of(
                transacaoPontual(TipoTransacao.RECEITA, 1000.0, d0),
                transacaoPontual(TipoTransacao.GASTO,    200.0, d2)
        );

        List<SaldoDiario> snapshots = recalcular(d0, transacoes);

        assertThat(saldoEm(snapshots, d0).getSaldoFinal()).isEqualByComparingTo("1000.00");
        assertThat(saldoEm(snapshots, d0.plusDays(1)).getSaldoFinal()).isEqualByComparingTo("1000.00");
        assertThat(saldoEm(snapshots, d2).getSaldoFinal()).isEqualByComparingTo("800.00");
        assertThat(saldoEm(snapshots, d2.plusDays(1)).getSaldoFinal()).isEqualByComparingTo("800.00");
        assertThat(saldoEm(snapshots, d2.plusDays(30)).getSaldoFinal()).isEqualByComparingTo("800.00");
    }

    @Test
    void seedDoUltimoSaldoAnterior_usadoComoInicio() {
        LocalDate dataMinima = LocalDate.now();

        // simula que já existe saldo_diario com saldoFinal = 2000 antes da dataMinima
        SaldoDiario saldoAnterior = SaldoDiario.builder()
                .saldoFinal(new BigDecimal("2000.00"))
                .build();
        when(saldoDiarioRepository.buscarUltimoAntesDeData(instId, dataMinima))
                .thenReturn(Optional.of(saldoAnterior));

        var transacoes = List.of(transacaoPontual(TipoTransacao.GASTO, 500.0, dataMinima));

        List<SaldoDiario> snapshots = recalcular(dataMinima, transacoes);

        SaldoDiario sd = saldoEm(snapshots, dataMinima);
        assertThat(sd.getSaldoInicial()).isEqualByComparingTo("2000.00");
        assertThat(sd.getSaldoFinal()).isEqualByComparingTo("1500.00");
    }

    @Test
    void semTransacoes_saldoPermaneceZeroTodosPeriodo() {
        LocalDate dataMinima = LocalDate.now();

        List<SaldoDiario> snapshots = recalcular(dataMinima, List.of());

        // todos os dias devem ter saldo zero
        snapshots.forEach(sd -> {
            assertThat(sd.getTotalReceitas()).isEqualByComparingTo("0.00");
            assertThat(sd.getTotalGastos()).isEqualByComparingTo("0.00");
            assertThat(sd.getSaldoFinal()).isEqualByComparingTo("0.00");
        });
    }

    @Test
    void transacaoFuturaAoDataMinima_naoAfetaDiasAnteriores() {
        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);

        // transação só em amanhã — recalcular a partir de hoje
        var transacoes = List.of(transacaoPontual(TipoTransacao.RECEITA, 800.0, amanha));

        List<SaldoDiario> snapshots = recalcular(hoje, transacoes);

        // hoje: sem receita
        assertThat(saldoEm(snapshots, hoje).getTotalReceitas()).isEqualByComparingTo("0.00");
        assertThat(saldoEm(snapshots, hoje).getSaldoFinal()).isEqualByComparingTo("0.00");

        // amanhã: tem a receita
        assertThat(saldoEm(snapshots, amanha).getTotalReceitas()).isEqualByComparingTo("800.00");
        assertThat(saldoEm(snapshots, amanha).getSaldoFinal()).isEqualByComparingTo("800.00");
    }

    @Test
    void recalcular_deletaSnapshotsAPartirDataMinimaERecria() {
        LocalDate dataMinima = LocalDate.now();
        when(transacaoRepository.findAllAtivasByInstituicao(instId)).thenReturn(List.of());

        service.recalcular(instId, dataMinima);

        verify(saldoDiarioRepository).deletarAPartirDeData(instId, dataMinima);
        verify(saldoDiarioRepository).salvarTodos(any());
    }

    @Test
    void instituicaoNaoEncontrada_lancaIllegalStateException() {
        UUID idInexistente = UUID.randomUUID();
        when(instituicaoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recalcular(idInexistente, LocalDate.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Instituição não encontrada");
    }

    @Test
    void cenarioCompleto_salarioMensalAluguelMensalGastosPontuais() {
        // Simula um mês completo: salário no dia 5, aluguel no dia 10, mercado no dia 15
        LocalDate mes = LocalDate.now().withDayOfMonth(1);
        LocalDate d5  = mes.withDayOfMonth(5);
        LocalDate d10 = mes.withDayOfMonth(10);
        LocalDate d15 = mes.withDayOfMonth(15);

        var transacoes = List.of(
                transacaoRecorrente(TipoTransacao.RECEITA, 3500.0, d5,  Recorrencia.MENSAL, d5.plusMonths(6)),
                transacaoRecorrente(TipoTransacao.GASTO,   1200.0, d10, Recorrencia.MENSAL, d10.plusMonths(6)),
                transacaoPontual(TipoTransacao.GASTO,       320.0, d15)
        );

        List<SaldoDiario> snapshots = recalcular(mes, transacoes);

        // antes do dia 5: saldo zero
        assertThat(saldoEm(snapshots, mes).getSaldoFinal()).isEqualByComparingTo("0.00");

        // após salário: 3500
        assertThat(saldoEm(snapshots, d5).getSaldoFinal()).isEqualByComparingTo("3500.00");

        // após aluguel: 3500 - 1200 = 2300
        assertThat(saldoEm(snapshots, d10).getSaldoFinal()).isEqualByComparingTo("2300.00");

        // após mercado: 2300 - 320 = 1980
        assertThat(saldoEm(snapshots, d15).getSaldoFinal()).isEqualByComparingTo("1980.00");

        // saldo propaga até fim do mês
        assertThat(saldoEm(snapshots, mes.withDayOfMonth(mes.lengthOfMonth())).getSaldoFinal())
                .isEqualByComparingTo("1980.00");

        // próximo mês: salário de novo → 1980 + 3500 = 5480
        assertThat(saldoEm(snapshots, d5.plusMonths(1)).getSaldoFinal()).isEqualByComparingTo("5480.00");
    }
}
