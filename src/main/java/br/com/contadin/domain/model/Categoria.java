package br.com.contadin.domain.model;

public class Categoria {
    private Integer id;
    private String nome;
    private Integer fkUsuario;

    public Categoria(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
