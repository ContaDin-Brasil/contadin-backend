package br.com.contadin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Integer id;
    private String nome;
    private Integer fkUsuario;
}
