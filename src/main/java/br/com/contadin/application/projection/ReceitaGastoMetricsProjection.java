package br.com.contadin.application.projection;


public record ReceitaGastoMetricsProjection(
        Double valorTotal,
        Long quantidade,
        Double valorMedio
) {}
