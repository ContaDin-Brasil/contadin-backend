package br.com.contadin.application.usecase.objetivo.helper;

import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.model.Objetivo;

import java.util.Comparator;

public final class ObjetivoOrdenacaoHelper {

    private ObjetivoOrdenacaoHelper() {}

    /**
     * Objetivos com prioridade primeiro (ALTA → MEDIA → BAIXA); sem prioridade por último.
     */
    public static Comparator<Objetivo> porPrioridadeDesc() {
        return Comparator
                .comparing((Objetivo o) -> o.getPrioridade() == null)
                .thenComparing(
                        Objetivo::getPrioridade,
                        Comparator.nullsLast(
                                Comparator.comparingInt(PrioridadeObjetivo::ordinal).reversed()));
    }
}
