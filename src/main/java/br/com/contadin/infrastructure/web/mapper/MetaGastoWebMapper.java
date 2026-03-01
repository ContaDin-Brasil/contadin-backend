package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.metaGasto.MetaGastoRequest;
import br.com.contadin.application.dto.metaGasto.MetaGastoResponse;
import br.com.contadin.domain.model.MetaGasto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MetaGastoWebMapper {

    @Mapping(target = "fkUsuario", source = "fkUsuario")
    @Mapping(target = "fkCategoria", source = "fkCategoria")
    MetaGasto toDomain(MetaGastoRequest request);

    MetaGastoResponse toResponse(MetaGasto request);
}
