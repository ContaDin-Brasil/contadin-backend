package br.com.contadin.application.dto.transacao;

import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoTransacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

public record TransacaoResponse(
        Integer id,
        Double valor,
        TipoTransacao tipo,
        String descricao,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime data_transacao,
        Boolean parcelado,
        Recorrencia recorrencia,
        @JsonFormat(pattern = "dd/MM/yyyy")
        Date fim_recorrencia,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime criadoEm,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime atualizadoEm
) {
}