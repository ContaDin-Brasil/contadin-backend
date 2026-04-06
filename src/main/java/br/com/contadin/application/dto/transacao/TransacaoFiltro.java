package br.com.contadin.application.dto.transacao;

import br.com.contadin.domain.enums.TipoTransacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoFiltro(
        TipoTransacao tipo,
        UUID fkInstituicao,
        UUID fkCategoria,
        Double valorGte,
        Double valorLte,
        Boolean parcelado,
        Boolean recorrente,
        LocalDateTime dataTransacaoGte,
        LocalDateTime dataTransacaoLte,
        String search
) {
}
