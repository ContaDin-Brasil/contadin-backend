package br.com.contadin.infrastructure.persistence.entity;

import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "objetivo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjetivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_objetivo", nullable = false)
    private TipoObjetivo tipoObjetivo;

    @Enumerated(EnumType.STRING)
    private PrioridadeObjetivo prioridade;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "fk_usuario", nullable = false)
    private UUID fkUsuario;

    @Column(name = "fk_categoria", nullable = false)
    private UUID fkCategoria;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
