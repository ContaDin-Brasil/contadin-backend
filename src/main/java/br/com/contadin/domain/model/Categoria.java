package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.TipoCategoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private UUID id;
    private String nome;
    private String icone;
    private String cor;
    private TipoCategoria tipo;
    private UUID fkUsuario;
    @Builder.Default
    private Boolean ativo = true;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
