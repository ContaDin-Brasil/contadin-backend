package br.com.contadin.application.dto.dashboard;

import br.com.contadin.domain.enums.TipoTransacao;

import java.math.BigDecimal;

public record DashReceitaResponse (
            Integer mes,
            TipoTransacao tipo,
            Double valorTotal
){}
