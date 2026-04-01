package br.com.contadin.application.dto.transacao;

import br.com.contadin.domain.enums.TipoTransacao;

import java.time.LocalDateTime;

public record TransacaoFiltro(
        TipoTransacao tipo,
        Integer fkInstituicao,
        Integer fkCategoria,
        Double valorGte,
        Double valorLte,
        Boolean parcelado,
        Boolean recorrente,
        LocalDateTime dataTransacaoGte,
        LocalDateTime dataTransacaoLte,
        String search
) {
}
