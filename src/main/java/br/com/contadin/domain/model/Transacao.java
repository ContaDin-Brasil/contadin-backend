package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoTransacao;

import java.time.LocalDateTime;
import java.util.Date;

public class Transacao {
    private String id;
    private Double valor;
    private TipoTransacao tipo;
    private String descricao;
    private LocalDateTime dataTransacao;
    private boolean parcelado;
    private Recorrencia recorrencia;
    private Date fimRecorrencia;
    private Integer fkInstituicao;
    private Integer fkCategoria;
}
