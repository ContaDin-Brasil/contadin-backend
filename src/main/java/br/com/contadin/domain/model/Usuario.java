package br.com.contadin.domain.model;

import br.com.contadin.domain.valueobject.Email;
import lombok.Getter;

@Getter
public class Usuario {

    private Integer id;
    private String nome;
    private String sobrenome;
    private Email email;
    private String senha;
    private String telefone;
    private boolean ativo;

    public Usuario(String nome, String sobrenome, Email email, String senha) {
        validarObrigatorios(nome, sobrenome, email, senha);

        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
    }

    private void validarObrigatorios(
            String nome,
            String sobrenome,
            Email email,
            String senha
    ) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome inválido");

        if (sobrenome == null || sobrenome.isBlank())
            throw new IllegalArgumentException("Sobrenome inválido");

        if (email == null || email.getEmail().isBlank())
            throw new IllegalArgumentException("Email inválido");

        if (senha == null || senha.isBlank())
            throw new IllegalArgumentException("Senha inválida");
    }
}
