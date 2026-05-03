package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {
    private UUID id;
    private Double valor;
    private TipoTransacao tipo;
    private String descricao;
    private LocalDateTime dataTransacao;
    private Boolean parcelado;
    private Integer qtdParcelas;
    private Recorrencia recorrencia;
    private Date fimRecorrencia;
    private Boolean ativo;
    private UUID fkInstituicao;
    private UUID fkCategoria;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
