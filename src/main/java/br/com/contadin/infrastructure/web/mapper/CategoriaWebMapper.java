package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class CategoriaWebMapper {

    public Categoria toDomain(CategoriaRequest request) {

        Usuario usuario = Usuario.builder()
                .id(request.usuarioId())
                .build();

        return Categoria.builder()
                .nome(request.nome())
                .fkUsuario(request.usuarioId())
                .build();
    }
}
