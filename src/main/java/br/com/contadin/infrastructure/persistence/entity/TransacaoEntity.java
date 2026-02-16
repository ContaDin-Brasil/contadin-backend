package br.com.contadin.infrastructure.persistence.entity;

import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Table(name = "transacao")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double valor;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private String descricao;

    @Column(name = "data_transacao", nullable = false)
    private LocalDateTime dataTransacao;

    private boolean parcelado;

    @Enumerated(EnumType.STRING)
    private Recorrencia recorrencia;

    @Column(name = "fim_recorrencia")
    private Date fimRecorrencia;

    @Column(name = "fk_instituicao", nullable = false)
    private Integer fkInstituicao;

    @Column(name = "fk_categoria", nullable = false)
    private Integer fkCategoria;

}
