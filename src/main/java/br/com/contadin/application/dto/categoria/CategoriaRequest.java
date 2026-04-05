package br.com.contadin.application.dto.categoria;

import br.com.contadin.domain.enums.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CategoriaRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String icone,

        String cor,

        @NotNull(message = "Tipo é obrigatório")
        TipoCategoria tipo,

        @Positive(message = "fkUsuario deve ser maior que 0")
        UUID fkUsuario
) {
}
