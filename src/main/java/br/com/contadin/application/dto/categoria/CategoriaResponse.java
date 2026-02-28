package br.com.contadin.application.dto.categoria;

import br.com.contadin.domain.enums.TipoCategoria;

public record CategoriaResponse(
        Integer id,
        String nome,
        String icone,
        String cor,
        TipoCategoria tipo,
        Integer fkUsuario
) {
}
