package br.com.contadin.application.dto.categoria;

public record CategoriaResponse(
        Integer id,
        String nome,
        Integer usuarioId
) {
}
