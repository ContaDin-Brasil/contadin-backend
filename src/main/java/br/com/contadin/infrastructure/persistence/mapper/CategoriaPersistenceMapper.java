package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaPersistenceMapper {

    CategoriaEntity toEntity(Categoria categoria);

    Categoria toDomain(CategoriaEntity entity);
}