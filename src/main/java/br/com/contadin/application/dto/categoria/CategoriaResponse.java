package br.com.contadin.application.dto.categoria;

import br.com.contadin.domain.enums.TipoCategoria;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        String nome,
        String icone,
        String cor,
        TipoCategoria tipo,
        UUID fkUsuario,
        Boolean ativo,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime criadoEm,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime atualizadoEm
) {
}
