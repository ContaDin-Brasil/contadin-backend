package br.com.contadin.application.dto.saldo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaldoDiarioResponse(
        LocalDate data,
        BigDecimal saldoInicial,
        BigDecimal totalReceitas,
        BigDecimal totalGastos,
        BigDecimal saldoFinal
) {}
