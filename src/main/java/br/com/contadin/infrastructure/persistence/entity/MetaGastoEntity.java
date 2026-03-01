package br.com.contadin.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Table(name = "meta_gasto")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaGastoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;

    private  String nome;
    private  BigDecimal valor;
    private  Date dataFimMeta;
    private  LocalDateTime criadoEm;

    @Column(name = "fk_usuario", nullable = false)
    private  Integer fkUsuario;

    private  Integer fkCategoria;
}
