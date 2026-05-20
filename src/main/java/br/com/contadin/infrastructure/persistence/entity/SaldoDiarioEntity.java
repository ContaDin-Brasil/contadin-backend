package br.com.contadin.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
    name = "saldo_diario",
    uniqueConstraints = @UniqueConstraint(columnNames = {"fk_instituicao", "data"})
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaldoDiarioEntity {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @Column(name = "fk_instituicao", nullable = false)
    private UUID fkInstituicao;

    @Column(name = "fk_usuario", nullable = false)
    private UUID fkUsuario;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoInicial;

    @Column(name = "total_receitas", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalReceitas;

    @Column(name = "total_gastos", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalGastos;

    @Column(name = "saldo_final", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoFinal;

    @Column(name = "calculado_em")
    private LocalDateTime calculadoEm;
}
