package br.com.contadin.application.projection;

import java.math.BigDecimal;

public record ReceitaMetricsProjection(
        Double valorTotal,
        Long quantidade,
        Double valorMedio
) {}
