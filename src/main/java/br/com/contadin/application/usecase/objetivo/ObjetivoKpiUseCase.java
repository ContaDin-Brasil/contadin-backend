package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.dto.objetivo.kpi.ImpactoPrevistoKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.MaiorAlertaKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.NoRitmoKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.ObjetivoNoRitmoItem;
import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.port.in.objetivo.ObjetivoKpiInputPort;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.usecase.objetivo.helper.ObjetivoMetricasHelper;
import br.com.contadin.application.usecase.objetivo.helper.ObjetivoOrdenacaoHelper;
import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.model.Objetivo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjetivoKpiUseCase implements ObjetivoKpiInputPort {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final MaiorAlertaKpiResponse MAIOR_ALERTA_VAZIO =
            new MaiorAlertaKpiResponse("--", null, null, null);

    private final ObjetivoRepository objetivoRepository;
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public ImpactoPrevistoKpiResponse impactoPrevisto(
            UUID fkUsuario, LocalDate dataInicio, LocalDate dataFim, TipoObjetivo tipo) {
        Periodo p = resolverPeriodoComDefaultsMesAtual(dataInicio, dataFim);
        List<Objetivo> comMetricas = carregarComMetricas(fkUsuario, p, tipo);
        BigDecimal total = comMetricas.stream()
                .map(this::contribuicaoImpacto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ImpactoPrevistoKpiResponse(total);
    }

    @Override
    public NoRitmoKpiResponse noRitmo(UUID fkUsuario, LocalDate dataInicio, LocalDate dataFim, TipoObjetivo tipo) {
        Periodo p = resolverPeriodoComDefaultsMesAtual(dataInicio, dataFim);
        List<Objetivo> comMetricas = carregarComMetricas(fkUsuario, p, tipo);
        List<ObjetivoNoRitmoItem> itens = comMetricas.stream()
                .filter(this::estaNoRitmo)
                .sorted(ObjetivoOrdenacaoHelper.porPrioridadeDesc())
                .map(this::toNoRitmoItem)
                .toList();
        return new NoRitmoKpiResponse(itens.size(), comMetricas.size(), itens);
    }

    @Override
    public MaiorAlertaKpiResponse maiorAlerta(UUID fkUsuario, LocalDate dataInicio, LocalDate dataFim, TipoObjetivo tipo) {
        Periodo p = resolverPeriodoComDefaultsMesAtual(dataInicio, dataFim);
        List<Objetivo> comMetricas = carregarComMetricas(fkUsuario, p, tipo);
        if (comMetricas.isEmpty()) {
            return MAIOR_ALERTA_VAZIO;
        }
        Objetivo pior = comMetricas.stream()
                .max(Comparator.comparing(this::scoreAlerta).thenComparing(this::gapMonetario))
                .orElseThrow();
        String nomeCategoria = categoriaRepository.findById(pior.getFkCategoria())
                .map(c -> c.getNome())
                .orElse(pior.getNome());
        String texto = formatarMaiorAlerta(nomeCategoria, pior);
        return new MaiorAlertaKpiResponse(texto, pior.getId(), pior.getStatus(), pior.getTipoObjetivo());
    }

    private BigDecimal contribuicaoImpacto(Objetivo o) {
        if (o.getTipoObjetivo() == TipoObjetivo.LIMITE_GASTO) {
            BigDecimal economia = o.getRealizado().compareTo(o.getValor()) <= 0
                    ? o.getValor().subtract(o.getRealizado())
                    : o.getValor().multiply(BigDecimal.valueOf(2)).subtract(o.getRealizado());
            return economia.max(BigDecimal.ZERO);
        }
        return o.getRealizado().min(o.getValor()).max(BigDecimal.ZERO);
    }

    private ObjetivoNoRitmoItem toNoRitmoItem(Objetivo o) {
        return new ObjetivoNoRitmoItem(
                o.getId(), o.getNome(), o.getTipoObjetivo(), o.getStatus(), o.getPercentual());
    }

    private String formatarMaiorAlerta(String nomeCategoria, Objetivo o) {
        String pct = o.getPercentual().stripTrailingZeros().toPlainString();
        return switch (o.getTipoObjetivo()) {
            case LIMITE_GASTO -> nomeCategoria + ": " + pct + "% do limite usado";
            case AUMENTO_RECEITA -> nomeCategoria + ": " + pct + "% da meta de receita";
        };
    }

    private List<Objetivo> carregarComMetricas(UUID fkUsuario, Periodo p, TipoObjetivo tipo) {
        if (fkUsuario == null) {
            throw new ObjetivoInvalidoException("ID do usuário é obrigatório para busca");
        }
        return objetivoRepository.findByUsuario(fkUsuario).stream()
                .filter(o -> tipo == null || o.getTipoObjetivo() == tipo)
                .filter(o -> intersecta(o.getDataInicio(), o.getDataFim(), p.inicio(), p.fim()))
                .map(o -> ObjetivoMetricasHelper.calcular(o, transacaoRepository, p.inicio(), p.fim()))
                .toList();
    }

    private static boolean intersecta(LocalDate oi, LocalDate of, LocalDate qi, LocalDate qf) {
        return !of.isBefore(qi) && !oi.isAfter(qf);
    }

    private boolean estaNoRitmo(Objetivo o) {
        return switch (o.getTipoObjetivo()) {
            case LIMITE_GASTO -> o.getStatus() != StatusObjetivo.ACIMA_DO_COMBINADO;
            case AUMENTO_RECEITA -> o.getStatus() != StatusObjetivo.NO_CAMINHO;
        };
    }

    private BigDecimal scoreAlerta(Objetivo o) {
        return switch (o.getTipoObjetivo()) {
            case LIMITE_GASTO -> o.getPercentual();
            case AUMENTO_RECEITA -> CEM.subtract(o.getPercentual());
        };
    }

    private BigDecimal gapMonetario(Objetivo o) {
        return switch (o.getTipoObjetivo()) {
            case LIMITE_GASTO -> {
                BigDecimal excedente = o.getRealizado().subtract(o.getValor());
                yield excedente.compareTo(BigDecimal.ZERO) > 0 ? excedente : BigDecimal.ZERO;
            }
            case AUMENTO_RECEITA -> {
                BigDecimal falta = o.getValor().subtract(o.getRealizado());
                yield falta.compareTo(BigDecimal.ZERO) > 0 ? falta : BigDecimal.ZERO;
            }
        };
    }

    private static Periodo resolverPeriodoComDefaultsMesAtual(LocalDate dataInicio, LocalDate dataFim) {
        YearMonth ym = YearMonth.now();
        LocalDate ini = dataInicio != null ? dataInicio : ym.atDay(1);
        LocalDate fim = dataFim != null ? dataFim : ym.atEndOfMonth();
        if (ini.isAfter(fim)) {
            throw new ObjetivoInvalidoException("dataInicio deve ser anterior ou igual a dataFim");
        }
        return new Periodo(ini, fim);
    }

    private record Periodo(LocalDate inicio, LocalDate fim) {}
}
