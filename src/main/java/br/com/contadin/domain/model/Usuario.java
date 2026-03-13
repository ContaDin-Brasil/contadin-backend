package br.com.contadin.domain.model;

import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Usuario {
    private Integer id;
    private String nome;
    private String sobrenome;
    private Email email;
    private String senha;
    private Telefone telefone;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
