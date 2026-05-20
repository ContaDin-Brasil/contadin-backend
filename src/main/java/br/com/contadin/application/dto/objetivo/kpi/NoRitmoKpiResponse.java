package br.com.contadin.application.dto.objetivo.kpi;

import java.util.List;

public record NoRitmoKpiResponse(
        int objetivosNoRitmo,
        int totalObjetivos,
        List<ObjetivoNoRitmoItem> objetivos
) {}
