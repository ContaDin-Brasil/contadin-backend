package br.com.contadin.domain.valueobject;

import br.com.contadin.domain.exception.usuario.EmailInvalidoException;

public record Email(String valor) {

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new EmailInvalidoException("Email não pode ser vazio");
        }

        valor = valor.trim().toLowerCase();

        if (!valor.contains("@")) {
            throw new EmailInvalidoException("Email inválido");
        }
    }
}