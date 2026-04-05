package br.com.contadin.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Builder
@ToString
public class MetaGasto {

    private final UUID id;
    private final String nome;
    private final BigDecimal valor;
    private final Date dataFimMeta;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;
    private final UUID fkUsuario;
    private final UUID fkCategoria;
}
