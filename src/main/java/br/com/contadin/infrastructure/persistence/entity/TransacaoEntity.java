package br.com.contadin.infrastructure.persistence.entity;

import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoTransacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "transacao")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private Double valor;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoTransacao tipo;

    private String descricao;

    @Column(name = "data_transacao", nullable = false)
    private LocalDateTime dataTransacao;

    @NotNull
    private Boolean parcelado;

    @Column(name = "qtd_parcelas")
    private Integer qtdParcelas;

    @Enumerated(EnumType.STRING)
    private Recorrencia recorrencia;

    @Column(name = "fim_recorrencia")
    private Date fimRecorrencia;

    private Boolean ativo;

    @Column(name = "fk_instituicao", nullable = false)
    @NotNull
    private UUID fkInstituicao;

    @Column(name = "fk_categoria", nullable = false)
    private UUID fkCategoria;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

}
