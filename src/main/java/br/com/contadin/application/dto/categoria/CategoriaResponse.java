package br.com.contadin.application.dto.categoria;

import br.com.contadin.domain.enums.TipoCategoria;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CategoriaResponse(
        Integer id,
        String nome,
        String icone,
        String cor,
        TipoCategoria tipo,
        Integer fkUsuario,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime criadoEm,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime atualizadoEm
) {
}
