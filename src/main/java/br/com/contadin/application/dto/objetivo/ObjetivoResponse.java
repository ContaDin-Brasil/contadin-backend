package br.com.contadin.application.dto.objetivo;

import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ObjetivoResponse(
        UUID id,
        String nome,
        String descricao,
        TipoObjetivo tipoObjetivo,
        BigDecimal valor,
        BigDecimal realizado,
        BigDecimal percentual,
        StatusObjetivo status,
        LocalDate dataInicio,
        LocalDate dataFim,
        PrioridadeObjetivo prioridade,
        UUID fkCategoria,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
