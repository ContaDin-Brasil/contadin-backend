package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.TipoInstituicao;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public final class Instituicao {

    private final UUID id;
    private final String nome;
    private final String icone;
    private final String cor;
    private final TipoInstituicao tipo;
    private final Integer fkUsuario;
    private final Boolean ativo;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;
}
