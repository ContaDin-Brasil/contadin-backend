package br.com.contadin.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Categoria {
    private Integer id;
    private String nome;
    private Integer fkUsuario;
}
