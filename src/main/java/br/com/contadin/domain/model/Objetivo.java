package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@ToString
public class Objetivo {

    private final UUID id;
    private final String nome;
    private final String descricao;
    private final BigDecimal valor;
    private final TipoObjetivo tipoObjetivo;
    private final PrioridadeObjetivo prioridade;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;
    private final UUID fkUsuario;
    private final UUID fkCategoria;

    // Campos calculados (não persistidos)
    private final BigDecimal realizado;
    private final BigDecimal percentual;
    private final StatusObjetivo status;
    private final String mensagemStatus;
}
