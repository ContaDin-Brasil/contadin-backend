package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ObjetivoProgresso {

    private ObjetivoProgresso() {}

    public static BigDecimal calcularPercentual(BigDecimal valor, BigDecimal realizado) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return realizado.multiply(BigDecimal.valueOf(100))
                .divide(valor, 2, RoundingMode.HALF_UP);
    }

    public static StatusObjetivo calcularStatus(TipoObjetivo tipo, BigDecimal percentual) {
        if (tipo == TipoObjetivo.AUMENTO_RECEITA) {
            return statusReceita(percentual);
        }
        return statusGasto(percentual);
    }

    public static String mensagem(StatusObjetivo status) {
        return switch (status) {
            case TRANQUILO -> "Tranquilo";
            case ATENCAO -> "Atenção";
            case CUIDADO -> "Cuidado";
            case ACIMA_DO_COMBINADO -> "Acima do combinado";
            case NO_CAMINHO -> "No caminho";
            case BOM_RITMO -> "Bom ritmo";
            case FALTA_POUCO -> "Falta pouco";
            case META_BATIDA -> "Meta batida";
        };
    }

    private static StatusObjetivo statusReceita(BigDecimal percentual) {
        if (percentual.compareTo(BigDecimal.valueOf(100)) >= 0) return StatusObjetivo.META_BATIDA;
        if (percentual.compareTo(BigDecimal.valueOf(75)) >= 0) return StatusObjetivo.FALTA_POUCO;
        if (percentual.compareTo(BigDecimal.valueOf(50)) >= 0) return StatusObjetivo.BOM_RITMO;
        return StatusObjetivo.NO_CAMINHO;
    }

    private static StatusObjetivo statusGasto(BigDecimal percentual) {
        if (percentual.compareTo(BigDecimal.valueOf(100)) >= 0) return StatusObjetivo.ACIMA_DO_COMBINADO;
        if (percentual.compareTo(BigDecimal.valueOf(75)) >= 0) return StatusObjetivo.CUIDADO;
        if (percentual.compareTo(BigDecimal.valueOf(50)) >= 0) return StatusObjetivo.ATENCAO;
        return StatusObjetivo.TRANQUILO;
    }
}
