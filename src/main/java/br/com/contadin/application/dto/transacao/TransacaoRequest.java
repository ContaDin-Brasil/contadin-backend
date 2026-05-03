package br.com.contadin.application.dto.transacao;

import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoTransacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record TransacaoRequest(
        Double valor,
        TipoTransacao tipo,
        String descricao,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dataTransacao,
        Boolean parcelado,
        Integer qtdParcelas,
        Recorrencia recorrencia,
        @JsonFormat(pattern = "yyyy-MM-dd")
        Date fimRecorrencia,
        Boolean ativo,
        UUID fkInstituicao,
        UUID fkCategoria
) { }