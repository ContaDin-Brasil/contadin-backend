package br.com.contadin.application.dto.categoria;

import java.math.BigDecimal;
import java.util.UUID;

public record GastoPorCategoriaResponse(
        UUID fkCategoria,
        String nome,
        String icone,
        String cor,
        BigDecimal total,
        BigDecimal percentual
) {}
