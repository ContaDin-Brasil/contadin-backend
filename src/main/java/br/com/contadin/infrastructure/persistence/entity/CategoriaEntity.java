package br.com.contadin.infrastructure.persistence.entity;

import br.com.contadin.domain.enums.TipoCategoria;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "categoria")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String icone;

    private String cor;

    @Enumerated(EnumType.STRING)
    private TipoCategoria tipo;

    @Column(name = "fk_usuario")
    private Integer fkUsuario;
}