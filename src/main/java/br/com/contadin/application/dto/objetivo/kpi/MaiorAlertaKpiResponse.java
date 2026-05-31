package br.com.contadin.application.dto.objetivo.kpi;

import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;

import java.util.UUID;

public record MaiorAlertaKpiResponse(
        String maiorAlerta,
        UUID objetivoId,
        StatusObjetivo status,
        TipoObjetivo tipoObjetivo
) {}
