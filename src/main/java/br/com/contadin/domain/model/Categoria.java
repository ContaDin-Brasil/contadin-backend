package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.TipoCategoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Integer id;
    private String nome;
    private String icone;
    private String cor;
    private TipoCategoria tipo;
    private Integer fkUsuario;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
