package br.com.contadin.application.dto.transacao;

import br.com.contadin.domain.enums.TipoTransacao;

import java.util.UUID;

public record TransacaoConsultaParams(
        Integer page,
        Integer limit,
        String sort,
        String order,
        TipoTransacao tipo,
        UUID fkInstituicao,
        UUID fkCategoria,
        Double valorGte,
        Double valorLte,
        Boolean parcelado,
        Boolean recorrente,
        String dataTransacaoGte,
        String dataTransacaoLte,
        String search
) {
}
