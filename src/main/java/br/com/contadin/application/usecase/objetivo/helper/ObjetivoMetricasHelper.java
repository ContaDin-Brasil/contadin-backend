package br.com.contadin.application.usecase.objetivo.helper;

import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.domain.model.ObjetivoProgresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class ObjetivoMetricasHelper {

    private ObjetivoMetricasHelper() {}

    public static Objetivo calcular(Objetivo objetivo, TransacaoRepository transacaoRepository) {
        TipoTransacao tipoTransacao = objetivo.getTipoObjetivo() == TipoObjetivo.LIMITE_GASTO
                ? TipoTransacao.GASTO
                : TipoTransacao.RECEITA;

        LocalDateTime inicio = objetivo.getDataInicio().atStartOfDay();
        LocalDateTime fim = objetivo.getDataFim().atTime(LocalTime.MAX);

        BigDecimal realizado = transacaoRepository.sumValorByCategoriaTipoEPeriodo(
                objetivo.getFkCategoria(), tipoTransacao, inicio, fim);

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
