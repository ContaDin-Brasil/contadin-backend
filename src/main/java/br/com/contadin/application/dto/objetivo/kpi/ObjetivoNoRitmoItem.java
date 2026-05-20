package br.com.contadin.application.dto.objetivo.kpi;

import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;

import java.math.BigDecimal;
import java.util.UUID;

public record ObjetivoNoRitmoItem(
        UUID id,
        String nome,
        TipoObjetivo tipoObjetivo,
        StatusObjetivo status,
        BigDecimal percentual
) {}
