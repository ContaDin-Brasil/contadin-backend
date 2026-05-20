package br.com.contadin.application.dto.saldo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaldoNaDataResponse(
        LocalDate data,
        BigDecimal saldoTotal,
        BigDecimal totalReceitas,
        BigDecimal totalGastos
) {}
