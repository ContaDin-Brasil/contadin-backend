package br.com.contadin.domain.model;

import br.com.contadin.domain.valueobject.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Usuario {

    private Integer id;
    private String nome;
    private String sobrenome;
    private Email email;
    private String senha;
    private String telefone;
    private boolean ativo;
}
