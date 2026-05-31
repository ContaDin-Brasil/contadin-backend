package br.com.contadin.application.dto.saldo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SaldoPorInstituicaoResponse(
        UUID fkInstituicao,
        LocalDate data,
        BigDecimal saldoFinal,
        BigDecimal totalReceitas,
        BigDecimal totalGastos
) {}
