package br.com.contadin.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "categoria")
public class CategoriaEntity {

    @Id
    private Integer id;
    private String nome;
    // private Integer fkUsuario;
}
