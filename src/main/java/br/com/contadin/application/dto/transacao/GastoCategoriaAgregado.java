package br.com.contadin.application.dto.transacao;

import java.math.BigDecimal;
import java.util.UUID;

public record GastoCategoriaAgregado(
        UUID fkCategoria,
        BigDecimal total
) {}
