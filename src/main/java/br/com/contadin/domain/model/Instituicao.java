package br.com.contadin.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public final class Instituicao {

    private final Integer id;
    private final String nome;
    private final String icone;
    private final String cor;
    private final Integer fkUsuario;
    private final boolean ativo;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;
}
