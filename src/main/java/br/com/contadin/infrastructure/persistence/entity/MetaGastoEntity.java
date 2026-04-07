package br.com.contadin.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "meta_gasto")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaGastoEntity {
    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    private  String nome;
    private  BigDecimal valor;
    private  Date dataFimMeta;
    private  LocalDateTime criadoEm;
    private  LocalDateTime atualizadoEm;

    @Column(name = "fk_usuario", nullable = false)
    private  UUID fkUsuario;

    private  UUID fkCategoria;
}
