package br.com.contadin.application.usecase.objetivo.helper;

import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.domain.model.ObjetivoProgresso;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class ObjetivoMetricasHelper {

    private ObjetivoMetricasHelper() {}

    public static Objetivo calcular(Objetivo objetivo, TransacaoRepository transacaoRepository) {
        return calcular(objetivo, transacaoRepository, objetivo.getDataInicio(), objetivo.getDataFim());
    }

    /**
     * Calcula realizado e status usando a interseção entre o período do objetivo e {@code periodoInicio}–{@code periodoFim}.
     */
    public static Objetivo calcular(
            Objetivo objetivo,
            TransacaoRepository transacaoRepository,
            LocalDate periodoInicio,
            LocalDate periodoFim) {
        TipoTransacao tipoTransacao = objetivo.getTipoObjetivo() == TipoObjetivo.LIMITE_GASTO
                ? TipoTransacao.GASTO
                : TipoTransacao.RECEITA;

        LocalDate ini = objetivo.getDataInicio().isAfter(periodoInicio) ? objetivo.getDataInicio() : periodoInicio;
        LocalDate fim = objetivo.getDataFim().isBefore(periodoFim) ? objetivo.getDataFim() : periodoFim;

        LocalDateTime inicio = ini.atStartOfDay();
        LocalDateTime fimDt = fim.atTime(LocalTime.MAX);

        BigDecimal realizado = transacaoRepository.sumValorByCategoriaTipoEPeriodo(
                objetivo.getFkCategoria(), tipoTransacao, inicio, fimDt);

        BigDecimal percentual = ObjetivoProgresso.calcularPercentual(objetivo.getValor(), realizado);
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(objetivo.getTipoObjetivo(), percentual);
        String mensagem = ObjetivoProgresso.mensagem(status);

        return objetivo.toBuilder()
                .realizado(realizado)
                .percentual(percentual)
                .status(status)
                .mensagemStatus(mensagem)
                .build();
    }
}
