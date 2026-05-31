package br.com.contadin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoDiario {
    private UUID id;
    private UUID fkInstituicao;
    private UUID fkUsuario;
    private LocalDate data;
    private BigDecimal saldoInicial;
    private BigDecimal totalReceitas;
    private BigDecimal totalGastos;
    private BigDecimal saldoFinal;
    private LocalDateTime calculadoEm;
}
