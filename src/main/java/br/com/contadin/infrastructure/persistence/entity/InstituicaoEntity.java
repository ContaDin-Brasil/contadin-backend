package br.com.contadin.infrastructure.persistence.entity;

import br.com.contadin.domain.enums.TipoInstituicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "instituicao")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstituicaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;

    private  String nome;
    private  String icone;
    private  String cor;

    @Enumerated(EnumType.STRING)
    private  TipoInstituicao tipo;

    @Column(name = "fk_usuario", nullable = false)
    private  Integer fkUsuario;

    private  Boolean ativo;

    private  LocalDateTime criadoEm;
    private  LocalDateTime atualizadoEm;
}
