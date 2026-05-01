package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.infrastructure.persistence.entity.ObjetivoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ObjetivoPersistenceMapper {

    @Mapping(target = "fkUsuario", source = "fkUsuario")
    ObjetivoEntity
    toEntity(Objetivo objetivo);

    @Mapping(target = "fkUsuario", source = "fkUsuario")
    Objetivo toDomain(ObjetivoEntity entity);
}
