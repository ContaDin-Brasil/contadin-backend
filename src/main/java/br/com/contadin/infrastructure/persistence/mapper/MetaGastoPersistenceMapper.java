package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.MetaGasto;
import br.com.contadin.infrastructure.persistence.entity.MetaGastoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MetaGastoPersistenceMapper {

    @Mapping(target = "fkUsuario", source = "fkUsuario")
    MetaGastoEntity toEntity(MetaGasto meta);

    @Mapping(target = "fkUsuario", source = "fkUsuario")
    MetaGasto toDomain(MetaGastoEntity entity);
}
