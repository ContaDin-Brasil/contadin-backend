package br.com.contadin.domain.valueobject;

public record Email(String valor) {

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }

        valor = valor.trim().toLowerCase();

        if (!valor.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
}