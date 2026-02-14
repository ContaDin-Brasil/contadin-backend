package br.com.contadin.domain.model;

import java.util.Date;

public class MetaGasto {

    private Integer id;
    private String nome;
    private Double valor;
    private Date dataFimMeta;
    private Integer fkUsuario;
    private Integer fkCategoria;
}
