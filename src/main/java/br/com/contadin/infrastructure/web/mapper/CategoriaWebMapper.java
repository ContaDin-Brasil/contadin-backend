package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.application.dto.categoria.CategoriaResponse;
import br.com.contadin.domain.model.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaWebMapper {

    Categoria toDomain(CategoriaRequest request);

    CategoriaResponse toResponse(Categoria request);
}
