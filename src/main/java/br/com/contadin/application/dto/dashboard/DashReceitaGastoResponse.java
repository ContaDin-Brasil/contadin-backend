package br.com.contadin.application.dto.dashboard;

import br.com.contadin.domain.enums.TipoTransacao;


public record DashReceitaGastoResponse(
            Integer mes,
            TipoTransacao tipo,
            Double valorTotal
){}
