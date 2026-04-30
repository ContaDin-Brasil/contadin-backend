package br.com.contadin.domain.model;

import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Usuario {
    private UUID id;
    private String nome;
    private String sobrenome;
    private Email email;
    private String senha;
    private Telefone telefone;

    @Builder.Default
    private boolean ativo = true;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
