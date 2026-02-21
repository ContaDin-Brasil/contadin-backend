package br.com.contadin.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
@ToString
public class MetaGasto {

    private final Integer id;
    private final String nome;
    private final BigDecimal valor;
    private final Date dataFimMeta;
    private final LocalDateTime criadoEm;
    private final Integer fkUsuario;
    private final Integer fkCategoria;
}
