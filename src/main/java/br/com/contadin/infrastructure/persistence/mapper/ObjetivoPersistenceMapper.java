package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.infrastructure.persistence.entity.ObjetivoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ObjetivoPersistenceMapper {

    @Mapping(target = "realizado", ignore = true)
    @Mapping(target = "percentual", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "mensagemStatus", ignore = true)
    Objetivo toDomain(ObjetivoEntity entity);

    @Mapping(target = "id", source = "id")
    ObjetivoEntity toEntity(Objetivo objetivo);
}
