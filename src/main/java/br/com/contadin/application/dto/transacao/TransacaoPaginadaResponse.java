package br.com.contadin.application.dto.transacao;

import java.util.List;

public record TransacaoPaginadaResponse(
        List<TransacaoResponse> data,
        int page,
        int limit,
        long total,
        int totalPages,
        boolean hasMore
) {
}
