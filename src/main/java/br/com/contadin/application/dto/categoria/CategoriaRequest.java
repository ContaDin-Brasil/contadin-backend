package br.com.contadin.application.dto.categoria;

import br.com.contadin.domain.enums.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String icone,

        String cor,

        @NotNull(message = "Tipo é obrigatório")
        TipoCategoria tipo,

        Integer fkUsuario
) {
}
